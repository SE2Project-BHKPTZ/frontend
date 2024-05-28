package at.aau.serg.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import at.aau.serg.R
import at.aau.serg.androidutils.ErrorUtils.showToast
import at.aau.serg.androidutils.GameUtils.cardItemToJson
import at.aau.serg.androidutils.GameUtils.convertSerializableToArray
import at.aau.serg.androidutils.GameUtils.getMaxRoundCount
import at.aau.serg.androidutils.GameUtils.getPlayerGameScreen
import at.aau.serg.fragments.TrickPredictionFragment
import at.aau.serg.models.CardItem
import at.aau.serg.models.Score
import at.aau.serg.models.Suit
import at.aau.serg.network.SocketHandler
import at.aau.serg.utils.CardsConverter
import at.aau.serg.utils.GameUtils.calculatePositionOfPlayer
import at.aau.serg.viewmodels.CardsViewModel
import at.aau.serg.viewmodels.GameScreenViewModel
import at.aau.serg.viewmodels.TrickPredictionViewModel
import org.json.JSONObject

class GameScreenActivity : AppCompatActivity() {

    private val trickViewModel: TrickPredictionViewModel by viewModels()
    private val cardsViewModel: CardsViewModel by viewModels()
    private val gameScreenViewModel: GameScreenViewModel by viewModels()
    private var cardPlayed: Boolean = false
    private var lastPlayedCard: CardItem? = null
    private var countPlayedCards = 0
    private lateinit var trumpCard: CardItem
    private var playerCount = 0
    private var allowedToPlayCard = false
    private var myPlayerIndex: Int = 0
    private var winnerPlayerIndex: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_game_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        updateFragmentContainerView(TrickPredictionFragment())
        initializeRoundCount()
        handleIntentData()
        setupSocketHandlers()
    }

    private fun handleIntentData() {
        val initialCards: Array<CardItem>?
        val initialTrumpCard: CardItem?

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            initialCards = intent.getSerializableExtra("cards", Array<CardItem>::class.java)
            initialTrumpCard = intent.getSerializableExtra("trump", CardItem::class.java)
        } else {
            initialCards = convertSerializableToArray(intent.getSerializableExtra("cards"))
            initialTrumpCard = intent.getSerializableExtra("trump") as? CardItem
        }

        myPlayerIndex = intent.getIntExtra("me", 0)
        playerCount = intent.getIntExtra("playerCount", 3)
        gameScreenViewModel.setPosition(myPlayerIndex)

        initialCards?.let { setCards(it) }
        initialTrumpCard?.let { setupTrumpCard(it) }
    }

    private fun setupTrumpCard(card: CardItem) {
        trumpCard = card
        val trumpImageView: ImageView = findViewById(R.id.ivTrumpCard)
        val cardResourceId = resources.getIdentifier("card_${card.suit.toString().lowercase()}_${card.value}", "drawable", packageName)
        setPlayerCard(trumpImageView, cardResourceId.takeIf { it != 0 } ?: R.drawable.card_diamonds_1)
    }

    private fun setupSocketHandlers() {
        SocketHandler.on("cardPlayed", ::cardPlayed)
        SocketHandler.on("nextSubround", ::nextSubRound)
        SocketHandler.on("startRound", ::startRound)
        SocketHandler.on("nextPlayer", ::nextPlayer)
        SocketHandler.on("score", ::updateScores)
    }

    fun btnMenuClicked(view: View){
        startActivity(Intent(this, MainActivity::class.java))
    }

    fun btnChangeFragmentClicked(view: View) {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerViewGame)
        val newFragment = when (fragment) {
            is TrickPredictionFragment -> getPlayerGameScreen(3)
            else -> TrickPredictionFragment()
        }

        if (newFragment != null) {
            cardPlayed = false
            updateFragmentContainerView(newFragment)
        }
    }

    private fun updateFragmentContainerView(newFragment: Fragment){
        runOnUiThread {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerViewGame, newFragment)
                .commitAllowingStateLoss()
        }
    }

    fun onCardClicked(cardItem: CardItem): Boolean {
        if(cardPlayed || allowedToPlayCard.not()) return false

        val player1CardImageView = findViewById<ImageView>(R.id.ivPlayer1Card)
        val cardResourceId = resources.getIdentifier("card_${cardItem.suit.toString().lowercase()}_${cardItem.value}", "drawable", packageName)
        setPlayerCard(player1CardImageView, cardResourceId)

        cardPlayed = true
        allowedToPlayCard = false
        lastPlayedCard = cardItem
        countPlayedCards++

        val json: JSONObject = cardItemToJson(cardItem)
        json.put("trump", trumpCard.suit)

        SocketHandler.emit("cardPlayed", json)
        return true
    }

    private fun cardPlayed(socketResponse: Array<Any>) {
        Log.d("Socket", "Received cardPlayed event")
        val cardPlayedResponse = socketResponse[0] as JSONObject
        val suit = cardPlayedResponse.getString("suit")
        val value = cardPlayedResponse.getString("value")
        val playerIdx = cardPlayedResponse.getInt("playerIdx")
        val winnerIdx = cardPlayedResponse.getInt("winnerIdx")

        val cardItem = CardItem(value, Suit.valueOf(suit))

        if(lastPlayedCard != null && cardItem == lastPlayedCard) {
            Log.d("Socket", "No card must be played")
            checkNewWinner(winnerIdx)
            return
        }
        countPlayedCards++

        val cardResourceId = resources.getIdentifier("card_${cardItem.suit.toString().lowercase()}_${cardItem.value}", "drawable", packageName)
        val positionOfPlayedCard = calculatePositionOfPlayer(playerIdx, myPlayerIndex, playerCount)
        val playerCardImageView = findViewById<ImageView>(resources.getIdentifier("ivPlayer${positionOfPlayedCard}Card", "id", packageName))
        setPlayerCard(playerCardImageView, cardResourceId)

        checkNewWinner(winnerIdx)
    }

    private fun setPlayerCard(cardImageView: ImageView, cardResourceId: Int){
        runOnUiThread {
            cardImageView.setImageResource(cardResourceId.takeIf { it != 0 } ?: R.drawable.card_diamonds_1)
            cardImageView.tag = cardResourceId
        }
    }

    private fun checkNewWinner(winnerIdx: Int){
        if(winnerIdx == winnerPlayerIndex)
            return

        highlightCard(calculatePositionOfPlayer(winnerIdx, myPlayerIndex, playerCount))
        winnerPlayerIndex = winnerIdx
    }

    private fun highlightCard(newWinnerPosition: Int){
        val newWinnerCardImageView = findViewById<ImageView>(resources.getIdentifier("ivPlayer${newWinnerPosition}Card", "id", packageName))
        runOnUiThread{
            newWinnerCardImageView.setBackgroundResource(R.drawable.card_border_selected)
            winnerPlayerIndex?.let {
                val oldWinnerCardImageView = findViewById<ImageView>(resources.getIdentifier("ivPlayer${calculatePositionOfPlayer(it, myPlayerIndex, playerCount)}Card", "id", packageName))
                oldWinnerCardImageView.setBackgroundResource(R.drawable.card_border_default)
            }
        }
    }

    private fun nextSubRound(socketResponse: Array<Any>){
        Log.d("Socket", "Received nextSubRound event")

        setPlayerGameScreen()
        clearCardPlayedEvents()
        isNextPlayer(socketResponse[0] as Int)
    }

    private fun isNextPlayer(nextPlayerIdx: Int){
        if (nextPlayerIdx == myPlayerIndex) {
            allowedToPlayCard = true
            showToast(this, "You are now!")
        }
    }

    private fun startRound(socketResponse: Array<Any>) {
        Log.d("Socket", "Received startRound event")
        val gameData = (socketResponse[0] as JSONObject)
        val cards = gameData.getJSONArray("hands").getJSONArray(myPlayerIndex)
        trumpCard = CardsConverter.convertCard(gameData.getJSONObject("trump"))

        if (cards != null) {
            setCards(CardsConverter.convertCards(cards))
        }

        val trumpImageView: ImageView = findViewById(R.id.ivTrumpCard)
        val cardResourceId = trumpImageView.context.resources.getIdentifier("card_${trumpCard.suit.toString().lowercase()}_${trumpCard.value}", "drawable", trumpImageView.context.packageName)
        setPlayerCard(trumpImageView, cardResourceId)

        clearCardPlayedEvents()
        updateFragmentContainerView(TrickPredictionFragment())
        increaseRoundCount()
    }

    private fun initializeRoundCount() {
        this.runOnUiThread {
            trickViewModel.setRound(1)
            findViewById<TextView>(R.id.tvRoundCount).text = getString(R.string.gameRoundPlaceholder, 1, getMaxRoundCount(playerCount))
        }
    }

    private fun increaseRoundCount() {
        this.runOnUiThread {
            trickViewModel.increaseRound()
            findViewById<TextView>(R.id.tvRoundCount).text = getString(R.string.gameRoundPlaceholder, trickViewModel.round.value ?: 0, getMaxRoundCount(playerCount))
        }
    }

    private fun nextPlayer(socketResponse: Array<Any>){
        Log.d("Socket", "Received nextPlayer event ")
        isNextPlayer(socketResponse[0] as Int)
    }

    private fun setCards(cards: Array<CardItem>){
        runOnUiThread {
            cardsViewModel.setCards(cards)
        }
    }

    private fun clearCardPlayedEvents(){
        cardPlayed = false
        lastPlayedCard = null
        countPlayedCards = 0
        winnerPlayerIndex = null
    }

    fun setPlayerGameScreen() {
        val gameScreenFragment = getPlayerGameScreen(playerCount)
        if(gameScreenFragment != null){
            updateFragmentContainerView(gameScreenFragment)
        }
    }

    private fun updateScores(socketResponse: Array<Any>){
        Log.d("Socket", "Received scores event")
        val scores = socketResponse[0] as JSONObject
        val scoresList = mutableListOf<Score>()

        scores.keys().forEach {
            val value = scores.getJSONObject(it)
            scoresList.add(Score(value.getString("score"), value.getInt("index")))
        }

        this.runOnUiThread {
            gameScreenViewModel.setScores(scoresList.toTypedArray())
        }
    }
}
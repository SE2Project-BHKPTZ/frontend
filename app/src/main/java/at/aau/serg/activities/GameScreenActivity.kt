package at.aau.serg.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import at.aau.serg.R
import at.aau.serg.fragments.GameScreenFivePlayersFragment
import at.aau.serg.fragments.GameScreenFourPlayersFragment
import at.aau.serg.fragments.GameScreenSixPlayersFragment
import at.aau.serg.fragments.GameScreenThreePlayersFragment
import at.aau.serg.fragments.TrickPredictionFragment
import at.aau.serg.models.CardItem
import at.aau.serg.models.Suit
import at.aau.serg.network.SocketHandler
import at.aau.serg.viewmodels.CardsViewModel
import at.aau.serg.viewmodels.TrickPredictionViewModel
import org.json.JSONArray
import org.json.JSONObject

class GameScreenActivity : AppCompatActivity() {
    private val trickViewModel: TrickPredictionViewModel by viewModels()
    private val cardsViewModel: CardsViewModel by viewModels()
    private var cardPlayed: Boolean = false
    private var lastPlayedCard: CardItem? = null
    private var countPlayedCards = 0
    private lateinit var trumpCard: CardItem
    private var playerCount = 0
    private var allowedToPlayCard = false

    private var myPlayerIndex: Int = 0

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
        trickViewModel.setRound(1)

        val initialCards: Array<CardItem>?
        val initialTrumpCard: CardItem?

        // Check version as getSerializableExtra changed for API version > 33
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            initialCards = intent.getSerializableExtra("cards", Array<CardItem>::class.java)
            initialTrumpCard = intent.getSerializableExtra("trump", CardItem::class.java)
            myPlayerIndex = intent.getSerializableExtra("me", Int::class.java)!!
        } else {
            initialCards = intent.getSerializableExtra("cards") as? Array<CardItem>
            initialTrumpCard = intent.getSerializableExtra("trump") as? CardItem
            myPlayerIndex = (intent.getSerializableExtra("me") as? Int)!!
        }
        playerCount = intent.getIntExtra("playerCount", 3)

        if (initialCards != null) {
            setCards(initialCards)
        }

        val trumpImageView: ImageView = findViewById<ImageView>(R.id.ivTrumpCard)
        val cardResourceId = trumpImageView.context.resources.getIdentifier(
            "card_${initialTrumpCard?.suit.toString().lowercase()}_${initialTrumpCard?.value}", "drawable", trumpImageView.context.packageName)
        trumpImageView.setImageResource(cardResourceId.takeIf { it != 0 } ?: R.drawable.card_diamonds_1)

        if (initialTrumpCard != null) {
            trumpCard = initialTrumpCard
        }

        SocketHandler.on("cardPlayed", ::cardPlayed)
        SocketHandler.on("trickPrediction", ::trickPrediction)
        SocketHandler.on("nextSubround", ::nextSubRound)
        SocketHandler.on("startRound", ::startRound)
        SocketHandler.on("nextPlayer", ::nextPlayer)
    }

    fun getPlayerGameScreen(playerCount: Int): Fragment? {
        return when (playerCount){
            3 -> GameScreenThreePlayersFragment()
            4 -> GameScreenFourPlayersFragment()
            5 -> GameScreenFivePlayersFragment()
            6 -> GameScreenSixPlayersFragment()
            else -> null
        }
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
        val player1CardImageView = findViewById<ImageView>(R.id.ivPlayer1Card)
        if(cardPlayed || allowedToPlayCard.not()) return false

        val cardResourceId = resources.getIdentifier("card_${cardItem.suit.toString().lowercase()}_${cardItem.value}", "drawable", packageName)
        setPlayerCard(player1CardImageView, cardResourceId)

        cardPlayed = true
        allowedToPlayCard = false
        lastPlayedCard = cardItem
        countPlayedCards += 1

        val json: JSONObject = cardItemToJson(cardItem)
        json.put("trump", trumpCard.suit)

        SocketHandler.emit("cardPlayed", json)
        return true
    }

    private fun cardItemToJson(cardItem: CardItem): JSONObject{
        val jsonObject = JSONObject()
        jsonObject.put("value", cardItem.value)
        jsonObject.put("suit", cardItem.suit.toString())
        return jsonObject
    }

    private fun cardPlayed(socketResponse: Array<Any>) {
        Log.d("Socket", "Received cardPlayed event")
        val suit = (socketResponse[0] as JSONObject).getString("suit")
        val value = (socketResponse[0] as JSONObject).getString("value")
        //val player = (socketResponse[0] as JSONObject).getString("player")

        val cardItem = CardItem(value, Suit.valueOf(suit))

        if(lastPlayedCard != null && cardItem == lastPlayedCard) {
            Log.d("Socket", "No card must be played")
            return
        }
        countPlayedCards += 1

        val cardResourceId = resources.getIdentifier("card_${cardItem.suit.toString().lowercase()}_${cardItem.value}", "drawable", packageName)
        val player2CardImageView = findViewById<ImageView>(R.id.ivPlayer2Card)

        if (cardPlayed){
            when(countPlayedCards){
                3 -> switchCardsWith3CardsPlayed(player2CardImageView)
                4 -> switchCardsWith4CardsPlayed(player2CardImageView)
                5 -> switchCardsWith5CardsPlayed(player2CardImageView)
                6 -> switchCardsWith6CardsPlayed(player2CardImageView)
                else -> {}
            }
            setPlayerCard(player2CardImageView, cardResourceId)
            return
        }

        when(countPlayedCards){
            2 -> switchCardsWith3CardsPlayed(player2CardImageView)
            3 -> switchCardsWith4CardsPlayed(player2CardImageView)
            4 -> switchCardsWith5CardsPlayed(player2CardImageView)
            5 -> switchCardsWith6CardsPlayed(player2CardImageView)
            else -> {}
        }
        setPlayerCard(player2CardImageView, cardResourceId)
    }

    private fun switchCardsWith3CardsPlayed(player2CardImageView: ImageView){
        val player3CardImageView = findViewById<ImageView>(R.id.ivPlayer3Card)
        switchCards(player2CardImageView, player3CardImageView)
    }

    private fun switchCardsWith4CardsPlayed(player2CardImageView: ImageView){
        val player3CardImageView = findViewById<ImageView>(R.id.ivPlayer3Card)
        val player4CardImageView = findViewById<ImageView>(R.id.ivPlayer4Card)
        switchCards(player3CardImageView, player4CardImageView)
        switchCards(player2CardImageView, player3CardImageView)
    }
    private fun switchCardsWith5CardsPlayed(player2CardImageView: ImageView){
        val player3CardImageView = findViewById<ImageView>(R.id.ivPlayer3Card)
        val player4CardImageView = findViewById<ImageView>(R.id.ivPlayer4Card)
        val player5CardImageView = findViewById<ImageView>(R.id.ivPlayer5Card)
        switchCards(player4CardImageView, player5CardImageView)
        switchCards(player3CardImageView, player4CardImageView)
        switchCards(player2CardImageView, player3CardImageView)
    }

    private fun switchCardsWith6CardsPlayed(player2CardImageView: ImageView){
        val player3CardImageView = findViewById<ImageView>(R.id.ivPlayer3Card)
        val player4CardImageView = findViewById<ImageView>(R.id.ivPlayer4Card)
        val player5CardImageView = findViewById<ImageView>(R.id.ivPlayer5Card)
        val player6CardImageView = findViewById<ImageView>(R.id.ivPlayer6Card)
        switchCards(player5CardImageView, player6CardImageView)
        switchCards(player4CardImageView, player5CardImageView)
        switchCards(player3CardImageView, player4CardImageView)
        switchCards(player2CardImageView, player3CardImageView)
    }


    private fun switchCards(ivFrom: ImageView, ivTo: ImageView){
        val resource = ivFrom.tag.toString().toInt()
        runOnUiThread{
            ivTo.setImageResource(resource)
            ivTo.tag = ivFrom.tag
        }
    }

    private fun setPlayerCard(cardImageView: ImageView, cardResourceId: Int){
        runOnUiThread {
            cardImageView.setImageResource(cardResourceId.takeIf { it != 0 } ?: R.drawable.card_diamonds_1)
            cardImageView.tag = cardResourceId
        }
    }

    private fun trickPrediction(socketResponse: Array<Any>) {
        Log.d("Socket", "Received trickPrediction event")
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
            runOnUiThread {
                Toast.makeText(this, "You are now!", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun startRound(socketResponse: Array<Any>) {
        Log.d("Socket", "Received startRound event")
        val gameData = (socketResponse[0] as JSONObject)
        val cards = gameData.getJSONArray("hands").getJSONArray(myPlayerIndex)
        val trumpCard = convertCard(gameData.getJSONObject("trump"))

        if (cards != null) {
            setCards(convertCards(cards))
        }

        val trumpImageView: ImageView = findViewById(R.id.ivTrumpCard)
        val cardResourceId = trumpImageView.context.resources.getIdentifier("card_${trumpCard.suit.toString().lowercase()}_${trumpCard.value}", "drawable", trumpImageView.context.packageName)
        setPlayerCard(trumpImageView, cardResourceId)

        val newFragment = TrickPredictionFragment()
        clearCardPlayedEvents()
        updateFragmentContainerView(newFragment)
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
    }

    private fun convertCards(cardsJsonArray: JSONArray): Array<CardItem> {
        val cardsList = mutableListOf<CardItem>()
        for (i in 0 until cardsJsonArray.length()) {
            val cardJson = cardsJsonArray.getJSONObject(i)
            val card = convertCard(cardJson)
            cardsList.add(card)
        }

        return cardsList.toTypedArray()
    }

    private fun convertCard(cardJson: JSONObject): CardItem {
        return CardItem(
            cardJson.getString("value"),
            stringToSuit(cardJson.getString("suit"))
        )
    }

    private fun stringToSuit(suitString: String): Suit {
        return when (suitString.uppercase()) {
            "HEARTS" -> Suit.HEARTS
            "DIAMONDS" -> Suit.DIAMONDS
            "CLUBS" -> Suit.CLUBS
            "SPADES" -> Suit.SPADES
            else -> throw IllegalArgumentException("Unknown suit: $suitString")
        }
    }

    fun setPlayerGameScreen() {
        val gameScreenFragment = getPlayerGameScreen(playerCount)
        if(gameScreenFragment != null){
            updateFragmentContainerView(gameScreenFragment)
        }
    }
}
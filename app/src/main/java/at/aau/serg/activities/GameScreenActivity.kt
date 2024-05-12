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
import at.aau.serg.utils.CardsConverter
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
        } else {
            initialCards = intent.getSerializableExtra("cards") as? Array<CardItem>
            initialTrumpCard = intent.getSerializableExtra("trump") as? CardItem
        }
        myPlayerIndex = intent.getIntExtra("me", 0)
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

        when(countPlayedCards){
            2 ->  if (cardPlayed.not())  moveCardPosition2To3()
            3 -> if(cardPlayed) moveCardPosition2To3() else moveCardPosition3To4()
            4 -> if(cardPlayed) moveCardPosition3To4() else moveCardPosition4To5()
            5 -> if (cardPlayed) moveCardPosition4To5() else moveCardPosition5To6()
            6 -> if(cardPlayed) moveCardPosition5To6()
            else -> {}
        }
        setPlayerCard(player2CardImageView, cardResourceId)
    }

    private fun moveCardPosition2To3(){
        switchCards(R.id.ivPlayer2Card, R.id.ivPlayer3Card)
    }

    private fun moveCardPosition3To4(){
        switchCards(R.id.ivPlayer3Card, R.id.ivPlayer4Card)
        moveCardPosition2To3()
    }
    private fun moveCardPosition4To5(){
        switchCards(R.id.ivPlayer4Card, R.id.ivPlayer5Card)
        moveCardPosition3To4()
    }

    private fun moveCardPosition5To6(){
        switchCards(R.id.ivPlayer5Card, R.id.ivPlayer6Card)
        moveCardPosition4To5()
    }


    private fun switchCards(ivFromId: Int, ivToId: Int){
        val ivFrom = findViewById<ImageView>(ivFromId)
        val ivTo = findViewById<ImageView>(ivToId)
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
        val trumpCard = CardsConverter.convertCard(gameData.getJSONObject("trump"))

        if (cards != null) {
            setCards(CardsConverter.convertCards(cards))
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

    fun setPlayerGameScreen() {
        val gameScreenFragment = getPlayerGameScreen(playerCount)
        if(gameScreenFragment != null){
            updateFragmentContainerView(gameScreenFragment)
        }
    }
}
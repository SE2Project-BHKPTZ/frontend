package at.aau.serg.models

import com.google.gson.*
import java.lang.reflect.Type
import java.io.Serializable

data class Score (val score: String, val position: Int): Serializable

class ScoreDeserializer : JsonDeserializer<Score> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Score {
        val jsonObject = json?.asJsonObject
        val score = jsonObject?.get("score")?.asString ?: ""
        val position = jsonObject?.get("index")?.asInt ?: 0
        return Score(score, position)
    }
}
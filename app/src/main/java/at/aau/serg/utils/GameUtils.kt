package at.aau.serg.utils

object GameUtils {
    fun calculatePositionOfPlayer(serverIdx: Int, localIdx: Int, playerCount: Int): Int{
        return when(localIdx - serverIdx){
            0 -> 1
            1 -> playerCount
            2 -> playerCount - 1
            3 -> playerCount - 2
            4 -> playerCount - 3
            5 -> playerCount - 4
            else -> 1 + ((localIdx - serverIdx) * -1)
        }
    }
}
package at.aau.serg.network

import android.content.ContextWrapper
import android.content.SharedPreferences
import at.aau.serg.logic.Authentication
import at.aau.serg.logic.StoreToken
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TokenStoreTest {

    private lateinit var storeToken: StoreToken
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var contextWrapper: ContextWrapper

    @BeforeEach
    fun setup() {
        storeToken = StoreToken()
        sharedPreferences = mockk()
        editor = mockk(relaxed = true)
        every { sharedPreferences.edit() } returns editor

        contextWrapper = mockk()
        every { contextWrapper.getSharedPreferences(any(), any()) } returns sharedPreferences
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `storeTokens should store access token and refresh token in SharedPreferences`() {
        val accessToken = "access_token"
        val refreshToken = "refresh_token"

        storeToken.storeTokens(accessToken, refreshToken, contextWrapper)

        verify {
            editor.putString("accessToken", "access_token")
            editor.putString("refreshToken", "refresh_token")
            editor.apply()
        }
    }

}
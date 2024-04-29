package at.aau.serg.network

import android.content.ContextWrapper
import android.content.SharedPreferences
import at.aau.serg.logic.Secret
import at.aau.serg.logic.StoreToken
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import org.json.JSONObject
import org.junit.Assert
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
        mockkObject(Secret)
        sharedPreferences = mockk()
        editor = mockk(relaxed = true)
        contextWrapper = mockk()
        storeToken = StoreToken(contextWrapper)

        every { sharedPreferences.edit() } returns editor
        every { Secret.getSecretSharedPref(any()) } returns sharedPreferences
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `storeTokens should store access token and refresh token in SharedPreferences`() {
        val accessToken = "access_token"
        val refreshToken = "refresh_token"

        storeToken.storeTokens(accessToken, refreshToken)

        verify {
            editor.putString("accessToken", "access_token")
            editor.putString("refreshToken", "refresh_token")
            editor.apply()
        }
    }

    @Test
    fun `storeAccessToken should store access token in SharedPreferences`(){
        val accessToken = "access_token"

        storeToken.storeAccessToken(accessToken)

        verify {
            editor.putString("accessToken", "access_token")
            editor.apply()
        }
    }

    @Test
    fun `storeTokenFromResponseBody should get json object`(){
        val jsonObject: JSONObject = mockk()

        every {jsonObject.getString("accessToken")} returns "access_token"
        every {jsonObject.getString("refreshToken")} returns "refresh_token"
        every { storeToken.storeTokens(any(), any()) } just Runs

        storeToken.storeTokenFromResponseBody(jsonObject)

        verify {
            storeToken.storeTokens("access_token", "refresh_token")
            jsonObject.getString("accessToken")
            jsonObject.getString("refreshToken")
        }
    }

    @Test
    fun `storeAccessTokenFromBody should get json object`(){
        val jsonObject: JSONObject = mockk()

        every {jsonObject.getString("accessToken")} returns "access_token"
        every { storeToken.storeAccessToken(any()) } just Runs

        storeToken.storeAccessTokenFromBody(jsonObject)

        verify {
            storeToken.storeAccessToken("access_token")
            jsonObject.getString("accessToken")
        }
    }

    @Test
    fun getAccessToken(){
        every { sharedPreferences.getString("accessToken", null) } returns "accessToken"

        val result = storeToken.getAccessToken()
        Assert.assertEquals("accessToken", result)

        verify {
            sharedPreferences.getString("accessToken", null)
        }
    }

    @Test
    fun getRefreshToken(){
        every { sharedPreferences.getString("refreshToken", null) } returns "refreshToken"

        val result = storeToken.getRefreshToken()
        Assert.assertEquals("refreshToken", result)

        verify {
            sharedPreferences.getString("refreshToken", null)
        }
    }

    @Test
    fun `storeUUID should store UUID in SharedPreferences`(){
        val uuid = "uuid"

        storeToken.storeUUID(uuid)

        verify {
            editor.putString("uuid", "uuid")
            editor.apply()
        }
    }

    @Test
    fun getUUID(){
        every { sharedPreferences.getString("uuid", null) } returns "uuid"

        val result = storeToken.getUUID()
        Assert.assertEquals("uuid", result)

        verify {
            sharedPreferences.getString("uuid", null)
        }
    }
}
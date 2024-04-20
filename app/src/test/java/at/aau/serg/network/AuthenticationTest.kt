package at.aau.serg.network

import android.content.ContextWrapper
import at.aau.serg.logic.Authentication
import at.aau.serg.logic.Secret
import at.aau.serg.logic.StoreToken
import at.aau.serg.models.User
import at.aau.serg.utils.App
import at.aau.serg.utils.Strings
import com.google.gson.Gson
import io.mockk.Called
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.verify
import okhttp3.Callback
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class AuthenticationTest {
    private lateinit var callback: Callback
    @BeforeEach
    fun setUp() {
        mockkObject(App.Companion)
        val mockApp = mockk<App>()
        every { App.instance } returns mockApp

        mockkStatic(Strings::class)
        every { Strings.get(any()) } returns "http://localhost:8080"

        mockkObject(HttpClient)
        callback = mockk(relaxed = true)
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `registerUser with valid credentials`() {
        val username = "testUser"
        val password = "testPassword"
        val userToRegister = User(username, password)
        val expectedRequestBody = Gson().toJson(userToRegister)

        every { HttpClient.post(any(), any(), any(), any()) } just Runs

        val result = Authentication.registerUser(username, password, callback)
        assertNull(result)
        verify {
            HttpClient.post(
                "users/register",
                expectedRequestBody,
                null,
                callback
            )
        }
    }

    @Test
    fun `registerUser with empty username`() {
        val password = "testPassword"

        val result = Authentication.registerUser("", password, callback)

        assertEquals("Username and Password cannot be empty", result)
        verify { callback wasNot  Called }
    }

    @Test
    fun `registerUser with empty password`() {
        val username = "testUser"

        val result = Authentication.registerUser(username, "", callback)

        assertEquals("Username and Password cannot be empty", result)
        verify { callback wasNot  Called }
    }

    @Test
    fun `loginUser with valid credentials`() {
        val username = "testUser"
        val password = "testPassword"
        val userToRegister = User(username, password)
        val expectedRequestBody = Gson().toJson(userToRegister)

        every { HttpClient.post(any(), any(), any(), any()) } just Runs

        val result = Authentication.loginUser(username, password, callback)
        assertNull(result)
        verify {
            HttpClient.post(
                "users/login",
                expectedRequestBody,
                null,
                callback
            )
        }
    }

    @Test
    fun `loginUser with empty username`() {
        val password = "testPassword"

        val result = Authentication.loginUser("", password, callback)

        assertEquals("Username and Password cannot be empty", result)
        verify { callback wasNot  Called }
    }

    @Test
    fun `loginUser with empty password`() {
        val username = "testUser"

        val result = Authentication.loginUser(username, "", callback)

        assertEquals("Username and Password cannot be empty", result)
        verify { callback wasNot  Called }
    }

    @Test
    fun `tokenValid successful`(){
        val context: ContextWrapper = mockk()
        val storeToken: StoreToken = mockk()
        val secret: Secret = mockk()

        every { storeToken.getAccessToken() } returns "access_token"
        every { HttpClient.get(any(), any(), any(), ) } just Runs

        val result = Authentication.tokenValid(callback, storeToken)

        assertTrue(result)
        verify {
            HttpClient.get(
                "users/me",
                "access_token",
                callback
            )
        }
    }

    @Test
    fun `tokenValid no access token`(){
        val storeToken: StoreToken = mockk()

        every { storeToken.getAccessToken() } returns null

        val result = Authentication.tokenValid(callback, storeToken)

        assertFalse(result)
        verify { callback wasNot  Called }
    }

    @Test
    fun `updateToken successful`(){
        val storeToken: StoreToken = mockk()

        every { storeToken.getRefreshToken() } returns "refreshToken"
        every { HttpClient.post(any(), any(), any(), any()) } just Runs

        val result = Authentication.updateToken(callback, storeToken)
        val jsonString = """
            {
            "refreshToken": "refreshToken"
            }
        """
        assertTrue(result)
        verify {
            HttpClient.post(
                "users/refresh",
                jsonString,
                null,
                callback
            )
        }
    }

    @Test
    fun `updateToken no refresh token`(){
        val storeToken: StoreToken = mockk()

        every { storeToken.getRefreshToken() } returns null

        val result = Authentication.updateToken(callback, storeToken)

        assertFalse(result)
        verify { callback wasNot  Called }
    }

}
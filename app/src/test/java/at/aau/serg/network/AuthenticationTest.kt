package at.aau.serg.network

import android.content.ContextWrapper
import at.aau.serg.logic.Authentication
import at.aau.serg.logic.Secret
import at.aau.serg.logic.StoreToken
import at.aau.serg.models.User
import com.google.gson.Gson
import io.mockk.Called
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import okhttp3.Callback
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class AuthenticationTest {

    private lateinit var authentication: Authentication
    private lateinit var httpClient: HttpClient
    private lateinit var callback: Callback
    @BeforeEach
    fun setUp() {
        httpClient = mockk()
        authentication = Authentication.getInstance(httpClient)
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

        every { httpClient.post(any(), any(), any(), any()) } just Runs

        val result = authentication.registerUser(username, password, callback)
        assertNull(result)
        verify {
            httpClient.post(
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

        val result = authentication.registerUser("", password, callback)

        assertEquals("Username and Password cannot be empty", result)
        verify { callback wasNot  Called }
    }

    @Test
    fun `registerUser with empty password`() {
        val username = "testUser"

        val result = authentication.registerUser(username, "", callback)

        assertEquals("Username and Password cannot be empty", result)
        verify { callback wasNot  Called }
    }

    @Test
    fun `loginUser with valid credentials`() {
        val username = "testUser"
        val password = "testPassword"
        val userToRegister = User(username, password)
        val expectedRequestBody = Gson().toJson(userToRegister)

        every { httpClient.post(any(), any(), any(), any()) } just Runs

        val result = authentication.loginUser(username, password, callback)
        assertNull(result)
        verify {
            httpClient.post(
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

        val result = authentication.loginUser("", password, callback)

        assertEquals("Username and Password cannot be empty", result)
        verify { callback wasNot  Called }
    }

    @Test
    fun `loginUser with empty password`() {
        val username = "testUser"

        val result = authentication.loginUser(username, "", callback)

        assertEquals("Username and Password cannot be empty", result)
        verify { callback wasNot  Called }
    }

    @Test
    fun `tokenValid successful`(){
        val context: ContextWrapper = mockk()
        val storeToken: StoreToken = mockk()
        val secret: Secret = mockk()

        every { storeToken.getAccessToken() } returns "access_token"
        every { httpClient.get(any(), any(), any(), ) } just Runs

        val result = authentication.tokenValid(callback, storeToken)

        assertTrue(result)
        verify {
            httpClient.get(
                "users/me",
                "access_token",
                callback
            )
        }
    }

    @Test
    fun `tokenValid no access token`(){
        val context: ContextWrapper = mockk()
        val storeToken: StoreToken = mockk()
        val secret: Secret = mockk()

        every { storeToken.getAccessToken() } returns null

        val result = authentication.tokenValid(callback, storeToken)

        assertFalse(result)
        verify { callback wasNot  Called }
    }

    @Test
    fun `updateToken successful`(){
        val context: ContextWrapper = mockk()
        val storeToken: StoreToken = mockk()
        val secret: Secret = mockk()

        every { storeToken.getRefreshToken() } returns "refreshToken"
        every { httpClient.post(any(), any(), any(), any()) } just Runs

        val result = authentication.updateToken(callback, storeToken)
        val jsonString = """
            {
            "refreshToken": "refreshToken"
            }
        """
        assertTrue(result)
        verify {
            httpClient.post(
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

        val result = authentication.updateToken(callback, storeToken)

        assertFalse(result)
        verify { callback wasNot  Called }
    }

}
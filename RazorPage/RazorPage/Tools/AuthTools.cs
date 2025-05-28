using Microsoft.AspNetCore.Authentication.Cookies;
using Microsoft.AspNetCore.Authentication;
using RazorPage.Models;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;
using System.Text.Json;
using RazorPage.DTO;

namespace RazorPage.Tools
{
    public class AuthTools
    {
        private readonly HttpClient _httpClient;
        private readonly IHttpContextAccessor _httpContextAccessor;

        // Constructor injecting HttpClient and IHttpContextAccessor dependencies
        public AuthTools(HttpClient httpClient, IHttpContextAccessor httpContextAccessor)
        {
            _httpClient = httpClient;
            _httpContextAccessor = httpContextAccessor;
        }

        /// <summary>
        /// Attempts to login the user by calling an external API and creating a cookie-based authentication session.
        /// </summary>
        /// <param name="loginModel">User credentials for login.</param>
        /// <returns>Tuple indicating success status and message.</returns>
        public async Task<(bool Success, string Message)> Login(LoginUser loginModel)
        {
            // Prepare the API request payload with user login data
            var apiRequest = new LoginUserDTO
            {
                Email = loginModel.Email,
                UserName = loginModel.Email, // Use email as username if no separate username exists
                PhoneNumber = "", // Default empty value
                Password = Convert.ToBase64String(Encoding.UTF8.GetBytes(loginModel.Password)) // Encode password as Base64
            };

            // Serialize the payload and prepare HTTP content
            var content = new StringContent(
                JsonSerializer.Serialize(apiRequest),
                Encoding.UTF8,
                "application/json");

            // Send POST request to the external API for user login
            var response = await _httpClient.PostAsync($"/api/Users", content);
            var body = await response.Content.ReadAsStringAsync();

            // Return failure if response indicates error
            if (!response.IsSuccessStatusCode)
            {
                return (false, body);
            }

            // On successful login, create claims for authentication cookie
            var claims = new List<Claim>
            {
                new Claim(ClaimTypes.Email, loginModel.Email),
                new Claim(ClaimTypes.Name, loginModel.Email)
            };

            // Create identity and principal for cookie authentication scheme
            var identity = new ClaimsIdentity(claims, CookieAuthenticationDefaults.AuthenticationScheme);
            var principal = new ClaimsPrincipal(identity);

            // Sign in the user with cookie authentication, setting expiration and persistence options
            await _httpContextAccessor.HttpContext.SignInAsync(
                CookieAuthenticationDefaults.AuthenticationScheme,
                principal,
                new AuthenticationProperties
                {
                    IsPersistent = loginModel.RememberMe, // Persist cookie if "Remember Me" checked
                    ExpiresUtc = DateTimeOffset.UtcNow.AddMinutes(30), // Set cookie expiry time
                    AllowRefresh = true
                });

            return (true, "Login successful");
        }

        /// <summary>
        /// Logs out the current user by signing out of the authentication scheme and deleting the API token cookie.
        /// </summary>
        public async Task LogoutAsync()
        {
            // Sign out of the cookie authentication scheme
            await _httpContextAccessor.HttpContext.SignOutAsync(
                CookieAuthenticationDefaults.AuthenticationScheme);

            // Define cookie options for secure deletion of the API token cookie
            var apiTokenCookieOptions = new CookieOptions
            {
                Expires = DateTimeOffset.Now.AddDays(-1), // Expire cookie immediately
                HttpOnly = true, // Restrict access to HTTP requests only
                Secure = true, // Cookie only sent over HTTPS
                SameSite = SameSiteMode.Strict, // Prevent cross-site cookie sending
            };

            // Delete the "ApiToken" cookie with the specified options
            _httpContextAccessor.HttpContext.Response.Cookies.Delete("ApiToken", apiTokenCookieOptions);
        }
    }
}

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

        public AuthTools(HttpClient httpClient, IHttpContextAccessor httpContextAccessor)
        {
            _httpClient = httpClient;
            _httpContextAccessor = httpContextAccessor;
        }

        public async Task<(bool Success, string Message)> Login(LoginUser loginModel)
        {
            // Crear el objeto que espera la API
            var apiRequest = new LoginUserDTO
            {
                Email = loginModel.Email,
                UserName = loginModel.Email, // Usar email como username si no tienes username específico
                PhoneNumber = "", // Valor por defecto vacío
                Password = Convert.ToBase64String(Encoding.UTF8.GetBytes(loginModel.Password))
            };

            var content = new StringContent(
                JsonSerializer.Serialize(apiRequest),
                Encoding.UTF8,
                "application/json");

            var response = await _httpClient.PostAsync($"/api/Users/{apiRequest.Email}, {apiRequest.Password}", content);
            var body = await response.Content.ReadAsStringAsync();

            if (!response.IsSuccessStatusCode)
            {
                return (false, body);
            }

            // Login exitoso - crear autenticación simple sin JWT
            var claims = new List<Claim>
            {
                new Claim(ClaimTypes.Email, loginModel.Email),
                new Claim(ClaimTypes.Name, loginModel.Email)
            };

            var identity = new ClaimsIdentity(claims, CookieAuthenticationDefaults.AuthenticationScheme);
            var principal = new ClaimsPrincipal(identity);

            await _httpContextAccessor.HttpContext.SignInAsync(
                CookieAuthenticationDefaults.AuthenticationScheme,
                principal,
                new AuthenticationProperties
                {
                    IsPersistent = loginModel.RememberMe,
                    ExpiresUtc = DateTimeOffset.UtcNow.AddMinutes(30),
                    AllowRefresh = true
                });

            return (true, "Login successful");
        }

        public async Task LogoutAsync()
        {
            await _httpContextAccessor.HttpContext.SignOutAsync(
                CookieAuthenticationDefaults.AuthenticationScheme);

            var apiTokenCookieOptions = new CookieOptions
            {
                Expires = DateTimeOffset.Now.AddDays(-1),
                HttpOnly = true,
                Secure = true,
                SameSite = SameSiteMode.Strict,
            };

            _httpContextAccessor.HttpContext.Response.Cookies.Delete("ApiToken", apiTokenCookieOptions);
        }
    }
}

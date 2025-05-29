using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;
using RazorPage.Models;
using System.Security.Cryptography;
using System.Text;
using System.Text.Json;

namespace RazorPage.Pages;

public class LoginModel : PageModel
{
    private readonly IHttpClientFactory _clientFactory;

    public LoginModel(IHttpClientFactory clientFactory)
    {
        _clientFactory = clientFactory;
    }

    [BindProperty]
    public string Email { get; set; }

    [BindProperty]
    public string Password { get; set; }

    public string ErrorMessage { get; set; }

    public async Task<IActionResult> OnPostAsync()
    {
        var client = _clientFactory.CreateClient();
        string encryptedPassword = EncryptPassword(Password);
        string url = $"http://localhost:7091/api/User/{Email},{encryptedPassword}";

        try
        {
            var response = await client.PostAsync(url, null);
            if (response.IsSuccessStatusCode)
            {
                var json = await response.Content.ReadAsStringAsync();
                var user = JsonSerializer.Deserialize<User>(json, new JsonSerializerOptions { PropertyNameCaseInsensitive = true });

                HttpContext.Session.SetString("UserData", JsonSerializer.Serialize(user));
                return RedirectToPage("/Map");
            }
            else
            {
                ErrorMessage = "Invalid login.";
                return Page();
            }
        }
        catch
        {
            ErrorMessage = "API connection failed.";
            return Page();
        }
    }

    private string EncryptPassword(string password)
    {
        var hasher = new PasswordHasher<User>();
        return hasher.HashPassword(null, password);
    }
}

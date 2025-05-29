using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;
using RazorPage.Models;
using System.Security.Cryptography;
using System.Text;
using System.Text.Json;

namespace RazorPage.Pages;

public class RegisterModel : PageModel
{
    private readonly IHttpClientFactory _clientFactory;

    public RegisterModel(IHttpClientFactory clientFactory)
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
        if (!ModelState.IsValid)
        {
            ErrorMessage = "Invalid form.";
            return Page();
        }

        var client = _clientFactory.CreateClient();
        string encryptedPassword = EncryptPassword(Password);

        var newUser = new User
        {
            Email = Email,
            Password = encryptedPassword,
            Rates = new List<Rate>() // If your API expects this
        };

        var json = JsonSerializer.Serialize(newUser);
        var content = new StringContent(json, Encoding.UTF8, "application/json");

        try
        {
            var response = await client.PostAsync("http://localhost:7091/api/User", content);

            if (response.IsSuccessStatusCode)
            {
                return RedirectToPage("/Login");
            }
            else
            {
                var responseText = await response.Content.ReadAsStringAsync();
                ErrorMessage = $"Registration failed: {response.StatusCode} - {responseText}";
                return Page();
            }
        }
        catch (Exception ex)
        {
            ErrorMessage = "API connection failed: " + ex.Message;
            return Page();
        }
    }

    private string EncryptPassword(string password)
    {
        using (SHA256 sha256 = SHA256.Create())
        {
            var hasher = new PasswordHasher<User>();
            return hasher.HashPassword(null, password);
        }
    }
}

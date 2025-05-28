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
        var client = _clientFactory.CreateClient();
        string encryptedPassword = EncryptPassword(Password);

        var newUser = new User
        {
            Email = Email,
            Password = encryptedPassword,
            Rates = new List<Rate>()
        };

        var json = JsonSerializer.Serialize(newUser);
        var content = new StringContent(json, Encoding.UTF8, "application/json");

        try
        {
            var response = await client.PostAsync("http://group4apiapi.azure-api.net/api/User", content);

            if (response.IsSuccessStatusCode)
            {
                return RedirectToPage("/Login");
            }
            else
            {
                ErrorMessage = "Registration failed. Email may already be in use.";
                return Page();
            }
        }
        catch
        {
            ErrorMessage = "Could not connect to API.";
            return Page();
        }
    }

    private string EncryptPassword(string password)
    {
        using (SHA256 sha256 = SHA256.Create())
        {
            byte[] bytes = Encoding.UTF8.GetBytes(password);
            byte[] hash = sha256.ComputeHash(bytes);
            return Convert.ToHexString(hash);
        }
    }
}

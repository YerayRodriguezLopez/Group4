using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;
using RazorPage.Models;
using RazorPage.Tools;

namespace RazorPage.Pages
{
    public class LoginModel : PageModel
    {
        private readonly AuthTools _authTools;

        public LoginModel(AuthTools authTools)
        {
            _authTools = authTools;
        }

        [BindProperty]
        public LoginUser User { get; set; }

        [TempData]
        public string ErrorMessage { get; set; }

        public void OnGet()
        {
            TempData.Clear();
        }

        public async Task<IActionResult> OnPostAsync(string returnUrl = null)
        {
            returnUrl ??= Url.Content("~/");

            if (!ModelState.IsValid)
            {
                return Page();
            }
                

            var result = await _authTools.Login(User);

            if (!result.Success)
            {
                ModelState.AddModelError(string.Empty, result.Message);
                return Page();
            }

            TempData["SuccessMessage"] = "Login successful! Redirecting...";
            return Page();
        }
    }
}

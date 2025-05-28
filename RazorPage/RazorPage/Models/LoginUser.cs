using System.ComponentModel.DataAnnotations;

namespace RazorPage.Models
{
    public class LoginUser
    {
        [Required(ErrorMessage = "Email is required")]
        [EmailAddress(ErrorMessage = "Invalid email format")]
        public string Email { get; set; }
        public string UserName { get; set; } = "";
        public string PhoneNumber { get; set; } = "";

        [Required(ErrorMessage = "Password is required")]
        
        public string Password { get; set; }

        public bool RememberMe { get; set; }
    }
}

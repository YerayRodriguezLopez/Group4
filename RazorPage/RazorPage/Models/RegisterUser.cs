using System.ComponentModel.DataAnnotations;
using System.ComponentModel;

namespace RazorPage.Models
{
    public class RegisterUser
    {
        [Required]
        [DisplayName("UserName")]
        public string UserName { get; set; }
        [Required]
        [DisplayName("PhoneNumber")]
        public string PhoneNumber {  get; set; }

        [Required]
        [EmailAddress]
        public string Email { get; set; }

        [Required]
        [DataType(DataType.Password)]
        [MinLength(8, ErrorMessage = "Password must be at least 8 characters long.")]
        [MaxLength(50, ErrorMessage = "Password cannot exceed 50 characters.")]
        [RegularExpression(@"^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).+$", ErrorMessage = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character.")]
        public string Password { get; set; }

        [DataType(DataType.Password)]
        [DisplayName("Confirm password")]
        [Compare("Password", ErrorMessage = "The password and the confirmation password must be the same!")]
        public string ConfirmationPassword { get; set; }
    }
}


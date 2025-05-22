namespace Group4API.DTOs
{
    public class RegisterUserModel
    {
        [System.ComponentModel.DataAnnotations.Required]
        [System.ComponentModel.DataAnnotations.EmailAddress]
        public string Email { get; set; }

        [System.ComponentModel.DataAnnotations.Required]
        [System.ComponentModel.DataAnnotations.StringLength(100, MinimumLength = 6)]
        public string Password { get; set; }
    }
}

namespace Group4API.DTOs
{
    public class UpdateUserModel
    {
        [System.ComponentModel.DataAnnotations.EmailAddress]
        public string Email { get; set; }

        public string CurrentPassword { get; set; }

        [System.ComponentModel.DataAnnotations.StringLength(100, MinimumLength = 6)]
        public string NewPassword { get; set; }
    }
}

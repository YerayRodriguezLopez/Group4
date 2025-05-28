namespace RazorPage.Models;


public class User
{
    public int Id { get; set; }
    public string Email { get; set; }
    public string Password { get; set; }
    public List<Rate> Rates { get; set; }
    public bool RememberMe { get; internal set; }
}

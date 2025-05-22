using Microsoft.AspNetCore.Identity;
using System.ComponentModel.DataAnnotations.Schema;
using System.ComponentModel.DataAnnotations;

namespace Group4API.Model
{
    public class User : IdentityUser
    {
        public List<Rate> Rates { get; set; } = new List<Rate>();
    }
}

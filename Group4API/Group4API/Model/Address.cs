using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace Group4API.Model
{
    public class Address
    {
        [Key]
        [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        public int Id { get; set; }
        public string Location { get; set; }
        public float Lat { get; set; }
        public float Lng { get; set; }
        public int CompanyId { get; set; }
        public Company Company { get; set; }
    }
}

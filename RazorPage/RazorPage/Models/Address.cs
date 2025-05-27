namespace RazorPage.Models
{
    public class Address
    {
        public int Id { get; set; }
        public string Location { get; set; }
        public float Lat { get; set; }
        public float Lng {  get; set; }
        public int CompanyId { get; set; }
    }
}

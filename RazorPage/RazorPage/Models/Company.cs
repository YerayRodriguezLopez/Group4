namespace RazorPage.Models
{
    public class Company
    {
        public int Id { get; set; }
        public string NIF { get; set; }
        public string Name { get; set; }
        public string Mail { get; set; }
        public int Phone { get; set; }
        public string Tags { get; set; }
        public float Score { get; set; }
        public string Retail { get; set; }
        public bool IsProvider { get; set; }
        public bool isRetail { get; set; }
        public Address Address { get; set; }
        public int RatingCounts { get; set; }
        public double AverageRating { get; set; }

    }
}

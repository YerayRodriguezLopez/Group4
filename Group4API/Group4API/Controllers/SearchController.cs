using Group4API.Context;
using Group4API.Model;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace Group4API.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class SearchController : ControllerBase
    {
        private readonly Group4DbContext _context;

        public SearchController(Group4DbContext context)
        {
            _context = context;
        }

        // GET: api/Search/companies?query=text&isProvider=true&isRetail=false&minScore=3.5
        [HttpGet("companies")]
        public async Task<ActionResult<IEnumerable<Company>>> SearchCompanies(
            [FromQuery] string query = null,
            [FromQuery] bool? isProvider = null,
            [FromQuery] bool? isRetail = null,
            [FromQuery] decimal? minScore = null,
            [FromQuery] string tags = null)
        {
            var companiesQuery = _context.Companies
                .Include(c => c.Address)
                .Include(c => c.Rates)
                .AsQueryable();

            // Filter by text query (name, NIF, mail)
            if (!string.IsNullOrEmpty(query))
            {
                companiesQuery = companiesQuery.Where(c =>
                    c.Name.Contains(query) ||
                    c.NIF.Contains(query) ||
                    c.Mail.Contains(query));
            }

            // Filter by isProvider
            if (isProvider.HasValue)
            {
                companiesQuery = companiesQuery.Where(c => c.IsProvider == isProvider.Value);
            }

            // Filter by isRetail
            if (isRetail.HasValue)
            {
                companiesQuery = companiesQuery.Where(c => c.IsRetail == isRetail.Value);
            }

            // Filter by minimum score
            if (minScore.HasValue)
            {
                companiesQuery = companiesQuery.Where(c => c.Score >= minScore.Value);
            }

            // Filter by tags
            if (!string.IsNullOrEmpty(tags))
            {
                var tagList = tags.Split(',').Select(t => t.Trim().ToLower()).ToList();
                companiesQuery = companiesQuery.Where(c =>
                    tagList.Any(tag => c.Tags.ToLower().Contains(tag)));
            }

            return await companiesQuery.ToListAsync();
        }

        // GET: api/Search/nearby?lat=41.123&lng=2.456&distance=5
        [HttpGet("nearby")]
        public async Task<ActionResult<IEnumerable<Company>>> GetNearbyCompanies(
            [FromQuery] float lat,
            [FromQuery] float lng,
            [FromQuery] float distance = 5.0f, // Default distance in kilometers
            [FromQuery] bool? isProvider = null)
        {
            // Get all companies with their addresses
            var companies = await _context.Companies
                .Include(c => c.Address)
                .Where(c => c.Address != null)
                .ToListAsync();

            // Filter by provider status if specified
            if (isProvider.HasValue)
            {
                companies = companies.Where(c => c.IsProvider == isProvider.Value).ToList();
            }

            // Calculate distance between current location and each company
            var results = companies
                .Select(c => new
                {
                    Company = c,
                    Distance = CalculateDistance(lat, lng, c.Address.Lat, c.Address.Lng)
                })
                .Where(result => result.Distance <= distance)
                .OrderBy(result => result.Distance)
                .Select(result => result.Company)
                .ToList();

            return results;
        }

        // Helper method to calculate distance between two points using Haversine formula
        private float CalculateDistance(float lat1, float lng1, float lat2, float lng2)
        {
            const float EarthRadiusKm = 6371.0f;

            var dLat = DegreeToRadian(lat2 - lat1);
            var dLng = DegreeToRadian(lng2 - lng1);

            var a =
                Math.Sin(dLat / 2) * Math.Sin(dLat / 2) +
                Math.Cos(DegreeToRadian(lat1)) * Math.Cos(DegreeToRadian(lat2)) *
                Math.Sin(dLng / 2) * Math.Sin(dLng / 2);

            var c = 2 * Math.Atan2(Math.Sqrt(a), Math.Sqrt(1 - a));
            var distance = EarthRadiusKm * c;

            return (float)distance;
        }

        private float DegreeToRadian(float degree)
        {
            return (float)(degree * Math.PI / 180);
        }
    }
}

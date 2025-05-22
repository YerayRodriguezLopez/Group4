using Group4API.Context;
using Group4API.Model;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace Group4API.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class RatesController : ControllerBase
    {
        private readonly Group4DbContext _context;

        public RatesController(Group4DbContext context)
        {
            _context = context;
        }

        // GET: api/Rates
        [HttpGet]
        public async Task<ActionResult<IEnumerable<Rate>>> GetRates()
        {
            return await _context.Rates
                .Include(r => r.User)
                .Include(r => r.Company)
                .ToListAsync();
        }

        // GET: api/Rates/5
        [HttpGet("{id}")]
        public async Task<ActionResult<Rate>> GetRate(int id)
        {
            var rate = await _context.Rates
                .Include(r => r.User)
                .Include(r => r.Company)
                .FirstOrDefaultAsync(r => r.Id == id);

            if (rate == null)
            {
                return NotFound();
            }

            return rate;
        }

        // GET: api/Rates/company/5
        [HttpGet("company/{companyId}")]
        public async Task<ActionResult<IEnumerable<Rate>>> GetRatesByCompany(int companyId)
        {
            var company = await _context.Companies.FindAsync(companyId);
            if (company == null)
            {
                return NotFound("Company not found");
            }

            return await _context.Rates
                .Include(r => r.User)
                .Where(r => r.CompanyId == companyId)
                .ToListAsync();
        }

        // GET: api/Rates/user/userId
        [HttpGet("user/{userId}")]
        public async Task<ActionResult<IEnumerable<Rate>>> GetRatesByUser(string userId)
        {
            var user = await _context.Users.FindAsync(userId);
            if (user == null)
            {
                return NotFound("User not found");
            }

            return await _context.Rates
                .Include(r => r.Company)
                .Where(r => r.UserId == userId)
                .ToListAsync();
        }

        // POST: api/Rates
        [HttpPost]
        public async Task<ActionResult<Rate>> PostRate(Rate rate)
        {
            // Verify that the referenced company exists
            var companyExists = await _context.Companies.AnyAsync(c => c.Id == rate.CompanyId);
            if (!companyExists)
            {
                return BadRequest("The specified company does not exist");
            }

            // Verify that the referenced user exists
            var userExists = await _context.Users.AnyAsync(u => u.Id == rate.UserId);
            if (!userExists)
            {
                return BadRequest("The specified user does not exist");
            }

            // Check if this user has already rated this company
            var existingRate = await _context.Rates
                .FirstOrDefaultAsync(r => r.UserId == rate.UserId && r.CompanyId == rate.CompanyId);

            if (existingRate != null)
            {
                return BadRequest("This user has already rated this company. Use PUT to update the rating.");
            }

            _context.Rates.Add(rate);
            await _context.SaveChangesAsync();

            // Update the company's overall score
            await UpdateCompanyScore(rate.CompanyId);

            return CreatedAtAction(nameof(GetRate), new { id = rate.Id }, rate);
        }

        // PUT: api/Rates/5
        [HttpPut("{id}")]
        public async Task<IActionResult> PutRate(int id, Rate rate)
        {
            if (id != rate.Id)
            {
                return BadRequest();
            }

            _context.Entry(rate).State = EntityState.Modified;

            try
            {
                await _context.SaveChangesAsync();

                // Update the company's overall score
                await UpdateCompanyScore(rate.CompanyId);
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!RateExists(id))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return NoContent();
        }

        // DELETE: api/Rates/5
        [HttpDelete("{id}")]
        public async Task<IActionResult> DeleteRate(int id)
        {
            var rate = await _context.Rates.FindAsync(id);
            if (rate == null)
            {
                return NotFound();
            }

            var companyId = rate.CompanyId;

            _context.Rates.Remove(rate);
            await _context.SaveChangesAsync();

            // Update the company's overall score
            await UpdateCompanyScore(companyId);

            return NoContent();
        }

        // Helper method to update company score based on rates
        private async Task UpdateCompanyScore(int companyId)
        {
            var company = await _context.Companies
                .Include(c => c.Rates)
                .FirstOrDefaultAsync(c => c.Id == companyId);

            if (company != null && company.Rates.Any())
            {
                company.Score = company.Rates.Average(r => r.Score);
                await _context.SaveChangesAsync();
            }
        }

        private bool RateExists(int id)
        {
            return _context.Rates.Any(e => e.Id == id);
        }
    }
}

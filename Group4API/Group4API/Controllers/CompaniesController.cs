using Group4API.Context;
using Group4API.Model;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace Group4API.Controllers
{
    /// <summary>
    /// Controller for managing companies in the database.
    /// </summary>
    [Route("api/[controller]")]
    [ApiController]
    public class CompaniesController : ControllerBase
    {
        private readonly Group4DbContext _context;

        /// <summary>
        /// Constructor for the CompaniesController.
        /// </summary>
        /// <param name="context"></param>
        public CompaniesController(Group4DbContext context)
        {
            _context = context;
        }

        // GET: api/Companies
        /// <summary>
        /// Retrieves all companies from the database.
        /// </summary>
        /// <returns></returns>
        [HttpGet]
        public async Task<ActionResult<IEnumerable<object>>> GetCompanies()
        {
            var companies = await _context.Companies
                .Include(c => c.Address)
                .Select(c => new {
                    c.Id,
                    c.NIF,
                    c.Name,
                    c.Mail,
                    c.Phone,
                    c.Tags,
                    c.Score,
                    c.IsProvider,
                    c.IsRetail,
                    Address = c.Address,
                    RatingsCount = c.Rates.Count(),
                    AverageRating = c.Rates.Any() ? c.Rates.Average(r => r.Score) : 0
                })
                .ToListAsync();

            return Ok(companies);
        }

        // GET: api/Companies/5
        /// <summary>
        /// Retrieves a specific company by its ID.
        /// </summary>
        /// <param name="id"></param>
        /// <returns></returns>
        [HttpGet("{id}")]
        public async Task<ActionResult<object>> GetCompany(int id)
        {
            var company = await _context.Companies
                .Include(c => c.Address)
                .Include(c => c.CompanyProviders)
                    .ThenInclude(cp => cp.Provider)
                        .ThenInclude(p => p.Address)
                .Where(c => c.Id == id)
                .Select(c => new {
                    c.Id,
                    c.NIF,
                    c.Name,
                    c.Mail,
                    c.Phone,
                    c.Tags,
                    c.Score,
                    c.IsProvider,
                    c.IsRetail,
                    Address = c.Address,
                    RatingsCount = c.Rates.Count(),
                    AverageRating = c.Rates.Any() ? c.Rates.Average(r => r.Score) : 0,
                    Providers = c.CompanyProviders.Select(cp => new {
                        cp.Provider.Id,
                        cp.Provider.Name,
                        cp.Provider.Mail,
                        cp.Provider.Phone,
                        cp.Provider.Tags,
                        cp.Provider.Score,
                        Address = cp.Provider.Address
                    }).ToList()
                })
                .FirstOrDefaultAsync();

            if (company == null)
            {
                return NotFound();
            }

            return Ok(company);
        }

        // GET: api/Companies/providers
        /// <summary>
        /// Retrieves all companies that are marked as providers.
        /// </summary>
        /// <returns></returns>
        [HttpGet("providers")]
        public async Task<ActionResult<IEnumerable<object>>> GetProviders()
        {
            var providers = await _context.Companies
                .Where(c => c.IsProvider)
                .Include(c => c.Address)
                .Select(c => new {
                    c.Id,
                    c.NIF,
                    c.Name,
                    c.Mail,
                    c.Phone,
                    c.Tags,
                    c.Score,
                    c.IsProvider,
                    c.IsRetail,
                    Address = c.Address,
                    RatingsCount = c.Rates.Count(),
                    AverageRating = c.Rates.Any() ? c.Rates.Average(r => r.Score) : 0,
                    ClientsCount = c.ProvidedCompanies.Count()
                })
                .ToListAsync();

            return Ok(providers);
        }

        // GET: api/Companies/5/providers
        /// <summary>
        /// Retrieves all providers associated with a specific company.
        /// </summary>
        /// <param name="id"></param>
        /// <returns></returns>
        [HttpGet("{id}/providers")]
        public async Task<ActionResult<IEnumerable<object>>> GetCompanyProviders(int id)
        {
            var company = await _context.Companies
                .Include(c => c.CompanyProviders)
                    .ThenInclude(cp => cp.Provider)
                        .ThenInclude(p => p.Address)
                .FirstOrDefaultAsync(c => c.Id == id);

            if (company == null)
            {
                return NotFound();
            }

            var providers = company.CompanyProviders.Select(cp => new {
                cp.Provider.Id,
                cp.Provider.NIF,
                cp.Provider.Name,
                cp.Provider.Mail,
                cp.Provider.Phone,
                cp.Provider.Tags,
                cp.Provider.Score,
                cp.Provider.IsProvider,
                cp.Provider.IsRetail,
                Address = cp.Provider.Address
            }).ToList();

            return Ok(providers);
        }

        // GET: api/Companies/5/clients
        /// <summary>
        /// Retrieves all clients associated with a specific provider company.
        /// </summary>
        /// <param name="id"></param>
        /// <returns></returns>
        [HttpGet("{id}/clients")]
        public async Task<ActionResult<IEnumerable<object>>> GetProviderClients(int id)
        {
            var company = await _context.Companies
                .Include(c => c.ProvidedCompanies)
                    .ThenInclude(cp => cp.Company)
                        .ThenInclude(c => c.Address)
                .FirstOrDefaultAsync(c => c.Id == id);

            if (company == null)
            {
                return NotFound();
            }

            if (!company.IsProvider)
            {
                return BadRequest("The specified company is not a provider");
            }

            var clients = company.ProvidedCompanies.Select(cp => new {
                cp.Company.Id,
                cp.Company.NIF,
                cp.Company.Name,
                cp.Company.Mail,
                cp.Company.Phone,
                cp.Company.Tags,
                cp.Company.Score,
                cp.Company.IsProvider,
                cp.Company.IsRetail,
                Address = cp.Company.Address
            }).ToList();

            return Ok(clients);
        }

        // GET: api/Companies/5/ratings
        /// <summary>
        /// Retrieves all ratings for a specific company.
        /// </summary>
        /// <param name="id"></param>
        /// <returns></returns>
        [HttpGet("{id}/ratings")]
        public async Task<ActionResult<IEnumerable<object>>> GetCompanyRatings(int id)
        {
            var companyExists = await _context.Companies.AnyAsync(c => c.Id == id);
            if (!companyExists)
            {
                return NotFound();
            }

            var ratings = await _context.Rates
                .Where(r => r.CompanyId == id)
                .Include(r => r.User)
                .Select(r => new {
                    r.Id,
                    r.Score,
                    UserEmail = r.User.Email
                })
                .ToListAsync();

            return Ok(ratings);
        }

        // POST: api/Companies
        /// <summary>
        /// Creates a new company in the database.
        /// </summary>
        /// <param name="company"></param>
        /// <returns></returns>
        [HttpPost]
        public async Task<ActionResult<Company>> PostCompany(Company company)
        {
            _context.Companies.Add(company);
            await _context.SaveChangesAsync();

            return CreatedAtAction(nameof(GetCompany), new { id = company.Id }, company);
        }

        // POST: api/Companies/5/providers/3
        /// <summary>
        /// Associates a provider with a company.
        /// </summary>
        /// <param name="companyId"></param>
        /// <param name="providerId"></param>
        /// <returns></returns>
        [HttpPost("{companyId}/providers/{providerId}")]
        public async Task<ActionResult> AddProviderToCompany(int companyId, int providerId)
        {
            var company = await _context.Companies.FindAsync(companyId);
            if (company == null)
            {
                return NotFound("Company not found");
            }

            var provider = await _context.Companies.FindAsync(providerId);
            if (provider == null)
            {
                return NotFound("Provider not found");
            }

            if (!provider.IsProvider)
            {
                return BadRequest("The specified provider is not marked as a provider");
            }

            // Check if the relationship already exists
            var existing = await _context.CompanyProviders
                .AnyAsync(cp => cp.CompanyId == companyId && cp.ProviderId == providerId);

            if (existing)
            {
                return BadRequest("This provider is already associated with the company");
            }

            var companyProvider = new CompanyProvider
            {
                CompanyId = companyId,
                ProviderId = providerId
            };

            _context.CompanyProviders.Add(companyProvider);
            await _context.SaveChangesAsync();

            return NoContent();
        }

        // PUT: api/Companies/5
        /// <summary>
        /// Updates an existing company in the database.
        /// </summary>
        /// <param name="id"></param>
        /// <param name="company"></param>
        /// <returns></returns>
        [HttpPut("{id}")]
        public async Task<IActionResult> PutCompany(int id, Company company)
        {
            if (id != company.Id)
            {
                return BadRequest();
            }

            _context.Entry(company).State = EntityState.Modified;

            try
            {
                await _context.SaveChangesAsync();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!CompanyExists(id))
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

        // DELETE: api/Companies/5
        /// <summary>
        /// Deletes a company from the database.
        /// </summary>
        /// <param name="id"></param>
        /// <returns></returns>
        [HttpDelete("{id}")]
        public async Task<IActionResult> DeleteCompany(int id)
        {
            var company = await _context.Companies.FindAsync(id);
            if (company == null)
            {
                return NotFound();
            }

            _context.Companies.Remove(company);
            await _context.SaveChangesAsync();

            return NoContent();
        }

        // DELETE: api/Companies/5/providers/3
        /// <summary>
        /// Removes a provider from a company.
        /// </summary>
        /// <param name="companyId"></param>
        /// <param name="providerId"></param>
        /// <returns></returns>
        [HttpDelete("{companyId}/providers/{providerId}")]
        public async Task<IActionResult> RemoveProviderFromCompany(int companyId, int providerId)
        {
            var companyProvider = await _context.CompanyProviders
                .FirstOrDefaultAsync(cp => cp.CompanyId == companyId && cp.ProviderId == providerId);

            if (companyProvider == null)
            {
                return NotFound("The provider relationship was not found");
            }

            _context.CompanyProviders.Remove(companyProvider);
            await _context.SaveChangesAsync();

            return NoContent();
        }

        /// <summary>
        /// Checks if a company exists in the database by its ID.
        /// </summary>
        /// <param name="id"></param>
        /// <returns></returns>
        private bool CompanyExists(int id)
        {
            return _context.Companies.Any(e => e.Id == id);
        }
    }
}
using Group4API.Context;
using Group4API.Model;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace Group4API.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class AddressesController : ControllerBase
    {
        private readonly Group4DbContext _context;

        public AddressesController(Group4DbContext context)
        {
            _context = context;
        }

        // GET: api/Addresses
        [HttpGet]
        public async Task<ActionResult<IEnumerable<Address>>> GetAddresses()
        {
            return await _context.Addresses.ToListAsync();
        }

        // GET: api/Addresses/5
        [HttpGet("{id}")]
        public async Task<ActionResult<Address>> GetAddress(int id)
        {
            var address = await _context.Addresses.FindAsync(id);

            if (address == null)
            {
                return NotFound();
            }

            return address;
        }

        // POST: api/Addresses
        [HttpPost]
        public async Task<ActionResult<Address>> PostAddress(Address address)
        {
            // Verify that the referenced company exists
            var companyExists = await _context.Companies.AnyAsync(c => c.Id == address.CompanyId);
            if (!companyExists)
            {
                return BadRequest("The specified company does not exist");
            }

            _context.Addresses.Add(address);
            await _context.SaveChangesAsync();

            return CreatedAtAction(nameof(GetAddress), new { id = address.Id }, address);
        }

        // PUT: api/Addresses/5
        [HttpPut("{id}")]
        public async Task<IActionResult> PutAddress(int id, Address address)
        {
            if (id != address.Id)
            {
                return BadRequest();
            }

            // Verify that the referenced company exists
            var companyExists = await _context.Companies.AnyAsync(c => c.Id == address.CompanyId);
            if (!companyExists)
            {
                return BadRequest("The specified company does not exist");
            }

            _context.Entry(address).State = EntityState.Modified;

            try
            {
                await _context.SaveChangesAsync();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!AddressExists(id))
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

        // DELETE: api/Addresses/5
        [HttpDelete("{id}")]
        public async Task<IActionResult> DeleteAddress(int id)
        {
            var address = await _context.Addresses.FindAsync(id);
            if (address == null)
            {
                return NotFound();
            }

            _context.Addresses.Remove(address);
            await _context.SaveChangesAsync();

            return NoContent();
        }

        private bool AddressExists(int id)
        {
            return _context.Addresses.Any(e => e.Id == id);
        }
    }
}

using Group4API.Context;
using Group4API.Model;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace Group4API.Controllers
{
    /// <summary>
    /// Controller for managing addresses in the database.
    /// </summary>
    [Route("api/[controller]")]
    [ApiController]
    public class AddressesController : ControllerBase
    {
        private readonly Group4DbContext _context;

        /// <summary>
        /// Constructor for the AddressesController.
        /// </summary>
        /// <param name="context"></param>
        public AddressesController(Group4DbContext context)
        {
            _context = context;
        }

        // GET: api/Addresses
        /// <summary>
        /// Retrieves all addresses from the database.
        /// </summary>
        /// <returns></returns>
        [HttpGet]
        public async Task<ActionResult<IEnumerable<Address>>> GetAddresses()
        {
            return await _context.Addresses.ToListAsync();
        }

        // GET: api/Addresses/5
        /// <summary>
        /// Retrieves a specific address by its ID.
        /// </summary>
        /// <param name="id"></param>
        /// <returns></returns>
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
        /// <summary>
        /// Creates a new address in the database.
        /// </summary>
        /// <param name="address"></param>
        /// <returns></returns>
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
        /// <summary>
        /// Updates an existing address in the database.
        /// </summary>
        /// <param name="id"></param>
        /// <param name="address"></param>
        /// <returns></returns>
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
        /// <summary>
        /// Deletes a specific address by its ID.
        /// </summary>
        /// <param name="id"></param>
        /// <returns></returns>
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
        /// <summary>
        /// Checks if an address exists in the database by its ID.
        /// </summary>
        /// <param name="id"></param>
        /// <returns></returns>
        private bool AddressExists(int id)
        {
            return _context.Addresses.Any(e => e.Id == id);
        }
    }
}

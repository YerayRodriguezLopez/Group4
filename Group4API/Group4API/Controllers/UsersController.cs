using Group4API.Context;
using Group4API.DTOs;
using Group4API.Model;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace Group4API.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class UsersController : ControllerBase
    {
        private readonly Group4DbContext _context;
        private readonly UserManager<User> _userManager;

        public UsersController(Group4DbContext context, UserManager<User> userManager)
        {
            _context = context;
            _userManager = userManager;
        }

        // GET: api/Users
        [HttpGet]
        public async Task<ActionResult<IEnumerable<User>>> GetUsers()
        {
            return await _context.Users
                .Include(u => u.Rates)
                .ToListAsync();
        }

        // GET: api/Users/5
        [HttpGet("{id}")]
        public async Task<ActionResult<User>> GetUser(string id)
        {
            var user = await _context.Users
                .Include(u => u.Rates)
                .FirstOrDefaultAsync(u => u.Id == id);

            if (user == null)
            {
                return NotFound();
            }

            return user;
        }

        // GET: api/Users/5/rates
        [HttpGet("{id}/rates")]
        public async Task<ActionResult<IEnumerable<Rate>>> GetUserRates(string id)
        {
            var user = await _context.Users
                .Include(u => u.Rates)
                    .ThenInclude(r => r.Company)
                .FirstOrDefaultAsync(u => u.Id == id);

            if (user == null)
            {
                return NotFound();
            }

            return user.Rates.ToList();
        }

        // POST: api/Users
        [HttpPost]
        public async Task<ActionResult<User>> RegisterUser(RegisterUserModel model)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            var user = new User
            {
                UserName = model.Email,
                Email = model.Email
            };

            var result = await _userManager.CreateAsync(user, model.Password);

            if (!result.Succeeded)
            {
                foreach (var error in result.Errors)
                {
                    ModelState.AddModelError(string.Empty, error.Description);
                }
                return BadRequest(ModelState);
            }

            return CreatedAtAction(nameof(GetUser), new { id = user.Id }, user);
        }

        // PUT: api/Users/5
        [HttpPut("{id}")]
        public async Task<IActionResult> UpdateUser(string id, UpdateUserModel model)
        {
            var user = await _userManager.FindByIdAsync(id);
            if (user == null)
            {
                return NotFound();
            }

            if (!string.IsNullOrEmpty(model.Email))
            {
                user.Email = model.Email;
                user.UserName = model.Email; // In this case username = email
            }

            if (!string.IsNullOrEmpty(model.CurrentPassword) && !string.IsNullOrEmpty(model.NewPassword))
            {
                var changePasswordResult = await _userManager.ChangePasswordAsync(user, model.CurrentPassword, model.NewPassword);
                if (!changePasswordResult.Succeeded)
                {
                    foreach (var error in changePasswordResult.Errors)
                    {
                        ModelState.AddModelError(string.Empty, error.Description);
                    }
                    return BadRequest(ModelState);
                }
            }

            var updateResult = await _userManager.UpdateAsync(user);
            if (!updateResult.Succeeded)
            {
                foreach (var error in updateResult.Errors)
                {
                    ModelState.AddModelError(string.Empty, error.Description);
                }
                return BadRequest(ModelState);
            }

            return NoContent();
        }

        // DELETE: api/Users/5
        [HttpDelete("{id}")]
        public async Task<IActionResult> DeleteUser(string id)
        {
            var user = await _userManager.FindByIdAsync(id);
            if (user == null)
            {
                return NotFound();
            }

            var result = await _userManager.DeleteAsync(user);
            if (!result.Succeeded)
            {
                foreach (var error in result.Errors)
                {
                    ModelState.AddModelError(string.Empty, error.Description);
                }
                return BadRequest(ModelState);
            }

            return NoContent();
        }
    }
}

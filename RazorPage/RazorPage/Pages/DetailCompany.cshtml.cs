using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;
using RazorPage.Models;
using RazorPage.Tools;

namespace RazorPage.Pages
{
    public class DetailCompanyModel : PageModel
    {
        // Dependency injected tool for accessing company data
        public readonly CompanyTools _companyTools;

        // Constructor to receive the CompanyTools instance via dependency injection
        public DetailCompanyModel(CompanyTools companyTools)
        {
            _companyTools = companyTools;
        }

        // Property to hold the company details to be displayed
        public Company Company { get; set; } = new();

        // Method to handle GET requests and load the company data by its ID asynchronously
        public async Task<IActionResult> OnGetAsync(int id)
        {
            // Retrieve the company details using the provided id
            Company = await _companyTools.GetCompanyByIdAsync(id);

            // Return the page with the company data populated
            return Page();
        }
    }
}

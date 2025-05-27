using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;
using RazorPage.Models;
using RazorPage.Tools;

namespace RazorPage.Pages
{
    public class DetailCompanyModel : PageModel
    {
		public readonly CompanyTools _companyTools;

		public DetailCompanyModel(CompanyTools companyTools)
		{
			_companyTools = companyTools;
		}

		public Company Company { get; set; } = new();

		public async Task<IActionResult> OnGetAsync(int id)
		{
			Company = await _companyTools.GetCompanyByIdAsync(id);
			return Page();
		}
	}
}

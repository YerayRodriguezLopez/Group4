using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;
using RazorPage.Models;
using RazorPage.Tools;

namespace RazorPage.Pages
{
    public class MapPageModel : PageModel
    {
        // Public properties for statistics to be displayed in the view
        public int TotalCompanies { get; private set; }
        public int ProvidersCount { get; private set; }
        public int NonProvidersCount { get; private set; }
        public int RetailCount { get; private set; }
        public int NonRetailCount { get; private set; }
        public int OnlyProviderCount { get; private set; }
        public int OnlyRetailCount { get; private set; }
        public int BothCount { get; private set; }
        public int NeitherCount { get; private set; }

        // Dependency injection
        private readonly AddressTools _addressTools;
        private readonly CompanyTools _companyTools;

        // Data for displaying on the map
        public List<Company> Companies { get; private set; } = new();
        public List<Address> Addresses { get; private set; } = new();

        public MapPageModel(AddressTools addressTools, CompanyTools companyTools)
        {
            _addressTools = addressTools;
            _companyTools = companyTools;
        }

        public async Task<IActionResult> OnGetAsync()
        {
            // Load companies and addresses data
            Addresses = await _addressTools.GetAddressAsync();
            Companies = await _companyTools.GetCompanyAsync();

            // Calculate statistics for the charts
            CalculateStatistics(Companies);

            return Page();
        }

        /// <summary>
        /// Calculates basic and combined statistics for companies.
        /// </summary>
        private void CalculateStatistics(List<Company> companies)
        {
            TotalCompanies = companies.Count;

            ProvidersCount = companies.Count(c => c.IsProvider);
            NonProvidersCount = companies.Count(c => !c.IsProvider);

            RetailCount = companies.Count(c => c.isRetail);
            NonRetailCount = companies.Count(c => !c.isRetail);

            OnlyProviderCount = companies.Count(c => c.IsProvider && !c.isRetail);
            OnlyRetailCount = companies.Count(c => !c.IsProvider && c.isRetail);
            BothCount = companies.Count(c => c.IsProvider && c.isRetail);
            NeitherCount = companies.Count(c => !c.IsProvider && !c.isRetail);
        }
    }
}

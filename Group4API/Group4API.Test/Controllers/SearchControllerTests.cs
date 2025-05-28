using Microsoft.AspNetCore.Mvc;
using Group4API.Controllers;
using Group4API.Model;
using Group4API.Tests.TestHelpers;
using Xunit;
using Group4API.Context;

namespace Group4API.Tests.Controllers
{
    public class SearchControllerTests : IDisposable
    {
        private readonly Group4DbContext _context;
        private readonly SearchController _controller;

        public SearchControllerTests()
        {
            _context = InMemoryDbContextFactory.Create();
            _controller = new SearchController(_context);
            SeedData();
        }

        private void SeedData()
        {
            var company1 = new Company
            {
                Id = 1,
                Name = "Tech Company",
                NIF = "12345678A",
                Mail = "tech@company.com",
                Phone = 123456789,
                Tags = "technology,software",
                Score = 4.5m,
                IsProvider = true,
                IsRetail = false,
                Address = new Address
                {
                    Id = 1,
                    Location = "Barcelona",
                    Lat = 41.3851f,
                    Lng = 2.1734f,
                    CompanyId = 1
                }
            };

            var company2 = new Company
            {
                Id = 2,
                Name = "Retail Store",
                NIF = "87654321B",
                Mail = "retail@store.com",
                Phone = 987654321,
                Tags = "retail,shopping",
                Score = 3.8m,
                IsProvider = false,
                IsRetail = true,
                Address = new Address
                {
                    Id = 2,
                    Location = "Madrid",
                    Lat = 40.4168f,
                    Lng = -3.7038f,
                    CompanyId = 2
                }
            };

            _context.Companies.AddRange(company1, company2);
            _context.SaveChanges();
        }

        [Fact]
        public async Task SearchCompanies_WithNameQuery_ReturnsMatchingCompanies()
        {
            // Act
            var result = await _controller.SearchCompanies("Tech");

            // Assert
            var okResult = Assert.IsType<ActionResult<IEnumerable<Company>>>(result);
            var companies = Assert.IsAssignableFrom<IEnumerable<Company>>(okResult.Value);
            Assert.Single(companies);
            Assert.Equal("Tech Company", companies.First().Name);
        }

        [Fact]
        public async Task SearchCompanies_WithProviderFilter_ReturnsOnlyProviders()
        {
            // Act
            var result = await _controller.SearchCompanies(isProvider: true);

            // Assert
            var okResult = Assert.IsType<ActionResult<IEnumerable<Company>>>(result);
            var companies = Assert.IsAssignableFrom<IEnumerable<Company>>(okResult.Value);
            Assert.Single(companies);
            Assert.True(companies.First().IsProvider);
        }

        [Fact]
        public async Task SearchCompanies_WithMinScoreFilter_ReturnsHighScoredCompanies()
        {
            // Act
            var result = await _controller.SearchCompanies(minScore: 4.0m);

            // Assert
            var okResult = Assert.IsType<ActionResult<IEnumerable<Company>>>(result);
            var companies = Assert.IsAssignableFrom<IEnumerable<Company>>(okResult.Value);
            Assert.Single(companies);
            Assert.True(companies.First().Score >= 4.0m);
        }

        [Fact]
        public async Task GetNearbyCompanies_WithValidCoordinates_ReturnsNearbyCompanies()
        {
            // Act - Search near Barcelona
            var result = await _controller.GetNearbyCompanies(41.4f, 2.2f, 50.0f);

            // Assert
            var okResult = Assert.IsType<ActionResult<IEnumerable<Company>>>(result);
            var companies = Assert.IsAssignableFrom<IEnumerable<Company>>(okResult.Value);
            Assert.NotEmpty(companies);
        }

        public void Dispose()
        {
            _context.Dispose();
        }
    }
}
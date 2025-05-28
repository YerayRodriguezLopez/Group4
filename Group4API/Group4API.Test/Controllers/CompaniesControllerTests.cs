
using Microsoft.AspNetCore.Mvc;
using Group4API.Controllers;
using Group4API.Model;
using Group4API.Tests.TestHelpers;
using Microsoft.AspNetCore.Identity;
using Moq;
using Xunit;
using Group4API.Context;

namespace Group4API.Tests.Controllers
{
    public class RatesControllerTests : IDisposable
    {
        private readonly Group4DbContext _context;
        private readonly RatesController _controller;

        public RatesControllerTests()
        {
            _context = InMemoryDbContextFactory.Create();
            _controller = new RatesController(_context);
            SeedData();
        }

        private void SeedData()
        {
            var user = new User
            {
                Id = "user1",
                UserName = "testuser@example.com",
                Email = "testuser@example.com"
            };

            var company = new Company
            {
                Id = 1,
                Name = "Test Company",
                NIF = "12345678A",
                Mail = "test@company.com",
                Phone = 123456789,
                Tags = "test",
                Score = 0m,
                IsProvider = true,
                IsRetail = false
            };

            var rate = new Rate
            {
                Id = 1,
                UserId = "user1",
                CompanyId = 1,
                Score = 4.5m,
                User = user,
                Company = company
            };

            _context.Users.Add(user);
            _context.Companies.Add(company);
            _context.Rates.Add(rate);
            _context.SaveChanges();
        }

        [Fact]
        public async Task GetRates_ReturnsAllRates()
        {
            // Act
            var result = await _controller.GetRates();

            // Assert
            var okResult = Assert.IsType<ActionResult<IEnumerable<Rate>>>(result);
            var rates = Assert.IsAssignableFrom<IEnumerable<Rate>>(okResult.Value);
            Assert.Single(rates);
        }

        [Fact]
        public async Task GetRate_WithValidId_ReturnsRate()
        {
            // Act
            var result = await _controller.GetRate(1);

            // Assert
            var okResult = Assert.IsType<ActionResult<Rate>>(result);
            var rate = Assert.IsType<Rate>(okResult.Value);
            Assert.Equal(4.5m, rate.Score);
        }

        [Fact]
        public async Task GetRatesByCompany_WithValidCompanyId_ReturnsRates()
        {
            // Act
            var result = await _controller.GetRatesByCompany(1);

            // Assert
            var okResult = Assert.IsType<ActionResult<IEnumerable<Rate>>>(result);
            var rates = Assert.IsAssignableFrom<IEnumerable<Rate>>(okResult.Value);
            Assert.Single(rates);
        }

        [Fact]
        public async Task PostRate_WithValidData_CreatesRate()
        {
            // Arrange
            var newRate = new Rate
            {
                UserId = "user1",
                CompanyId = 1,
                Score = 3.5m
            };

            // This should fail because user already rated this company
            // Act
            var result = await _controller.PostRate(newRate);

            // Assert
            Assert.IsType<BadRequestObjectResult>(result.Result);
        }

        [Fact]
        public async Task PostRate_WithNewUserCompanyCombination_CreatesRate()
        {
            // Arrange
            var newUser = new User
            {
                Id = "user2",
                UserName = "newuser@example.com",
                Email = "newuser@example.com"
            };
            _context.Users.Add(newUser);
            _context.SaveChanges();

            var newRate = new Rate
            {
                UserId = "user2",
                CompanyId = 1,
                Score = 3.5m
            };

            // Act
            var result = await _controller.PostRate(newRate);

            // Assert
            var createdResult = Assert.IsType<CreatedAtActionResult>(result.Result);
            var rate = Assert.IsType<Rate>(createdResult.Value);
            Assert.Equal(3.5m, rate.Score);
        }

        public void Dispose()
        {
            _context.Dispose();
        }
    }
}

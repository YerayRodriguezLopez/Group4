using Microsoft.AspNetCore.Mvc;
using Group4API.Controllers;
using Group4API.Model;
using Group4API.DTOs;
using Group4API.Tests.TestHelpers;
using Microsoft.AspNetCore.Identity;
using Moq;
using Xunit;
using Group4API.Context;

namespace Group4API.Tests.Controllers
{
    public class UsersControllerTests : IDisposable
    {
        private readonly Group4DbContext _context;
        private readonly Mock<UserManager<User>> _userManagerMock;
        private readonly UsersController _controller;

        public UsersControllerTests()
        {
            _context = InMemoryDbContextFactory.Create();
            _userManagerMock = CreateMockUserManager();
            _controller = new UsersController(_context, _userManagerMock.Object);
            SeedData();
        }

        private Mock<UserManager<User>> CreateMockUserManager()
        {
            var store = new Mock<IUserStore<User>>();
            return new Mock<UserManager<User>>(store.Object, null, null, null, null, null, null, null, null);
        }

        private void SeedData()
        {
            var user = new User
            {
                Id = "user1",
                UserName = "testuser@example.com",
                Email = "testuser@example.com"
            };

            _context.Users.Add(user);
            _context.SaveChanges();
        }

        [Fact]
        public async Task GetUsers_ReturnsAllUsers()
        {
            // Act
            var result = await _controller.GetUsers();

            // Assert
            var okResult = Assert.IsType<ActionResult<IEnumerable<User>>>(result);
            var users = Assert.IsAssignableFrom<IEnumerable<User>>(okResult.Value);
            Assert.Single(users);
        }

        [Fact]
        public async Task GetUser_WithValidId_ReturnsUser()
        {
            // Act
            var result = await _controller.GetUser("user1");

            // Assert
            var okResult = Assert.IsType<ActionResult<User>>(result);
            var user = Assert.IsType<User>(okResult.Value);
            Assert.Equal("user1", user.Id);
        }

        [Fact]
        public async Task GetUser_WithInvalidId_ReturnsNotFound()
        {
            // Act
            var result = await _controller.GetUser("invalid");

            // Assert
            Assert.IsType<NotFoundResult>(result.Result);
        }

        [Fact]
        public async Task RegisterUser_WithValidData_CreatesUser()
        {
            // Arrange
            var model = new RegisterUserModel
            {
                Email = "newuser@example.com",
                Password = "Password123!"
            };

            _userManagerMock.Setup(um => um.CreateAsync(It.IsAny<User>(), It.IsAny<string>()))
                .ReturnsAsync(IdentityResult.Success);

            // Act
            var result = await _controller.RegisterUser(model);

            // Assert
            var createdResult = Assert.IsType<CreatedAtActionResult>(result.Result);
            var user = Assert.IsType<User>(createdResult.Value);
            Assert.Equal("newuser@example.com", user.Email);
        }

        [Fact]
        public async Task RegisterUser_WithInvalidData_ReturnsBadRequest()
        {
            // Arrange
            var model = new RegisterUserModel
            {
                Email = "invalid-email",
                Password = "123" // Too short
            };

            _controller.ModelState.AddModelError("Email", "Invalid email format");

            // Act
            var result = await _controller.RegisterUser(model);

            // Assert
            Assert.IsType<BadRequestObjectResult>(result.Result);
        }

        public void Dispose()
        {
            _context.Dispose();
        }
    }
}
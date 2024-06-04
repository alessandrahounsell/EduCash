using educashAPI.Data;
using educashAPI.Data.Entity;
using educashAPI.Models;
using Microsoft.AspNetCore.Mvc;
using System.Collections.Immutable;

namespace educashAPI.Controllers
{
    [ApiController]
    [Route("[controller]")]
    public class PresetController : ControllerBase
    {
        private readonly ILogger<PresetController> _logger;
        private readonly EducashDbContext _educashDbContext;
        public PresetController(ILogger<PresetController> logger, EducashDbContext educashDbContext)
        {
            _logger = logger;
            _educashDbContext = educashDbContext;
        }

        //Get list of presets
        [HttpGet("GetPreset")]
        public IEnumerable<Preset> Get(string token)
        {
            var user = _educashDbContext.users.SingleOrDefault(x => x.Token ==  token);

            //Check to see if user is null
            if (user == null)
            {
                return new List<Preset>();
            }

            //Find default and user made presets
            var result = _educashDbContext.presets.Where(x => x.userId == null || x.userId == user.UserID).ToList();

            return result;
        }

        //Add own presets
        [HttpPost("addPreset")]
        public IEnumerable<Preset> Post(AddNewPresetModel preset ,string token)
        {
            //Find user by passed in token
            var user = _educashDbContext.users.SingleOrDefault(x => x.Token == token);

            //Check to see if user is null
            if (user == null)
            {
                return new List<Preset>();
            }

            //Add infomation
            var newPreset = new Preset
            {
                userId = user.UserID,
                PresetName = preset.Name,
                Price = preset.Price,
            };


            _educashDbContext.presets.Add(newPreset);

            //Try and save changes to database
            try
            {
                _educashDbContext.SaveChanges();
                return new List<Preset> { newPreset };
            }
            catch (Exception ex)
            {
                Response.StatusCode = 401;
                return new List<Preset>();
            }
        }

        //Remove own presets
        [HttpDelete]
        public bool Delete(int id,  string token)
        {
            //Find user by passed in token
            var user = _educashDbContext.users.SingleOrDefault(x => x.Token == token);

            //Chck if user is null
            if (user == null )
            {
                return false;
            }

            //Find preset by preset Id and user id
            var preset = _educashDbContext.presets.SingleOrDefault(x => x.PresetId == id && x.userId == user.UserID);

            //If not null remove
            if (preset != null)
            {
                _educashDbContext.Remove(preset);

                try
                {
                    _educashDbContext.SaveChanges();
                    return true;
                }
                catch (Exception ex)
                {
                    Response.StatusCode = 401;
                    return false;
                }
            }
            else
            {
                    Response.StatusCode = 401;
                    return false;  
            }
        }
    }
}

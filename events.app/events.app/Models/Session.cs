namespace events.app.Models;

public sealed class Session
{
    public string Id { get; set; } = string.Empty;
    public string EventId { get; set; } = string.Empty;
    public string EventName { get; set; } = string.Empty;
    public string Name { get; set; } = string.Empty;
    public string Description { get; set; } = string.Empty;
    public string Time { get; set; } = string.Empty;
    public string Room { get; set; } = string.Empty;
    public string Speaker { get; set; } = string.Empty;
    public string Type { get; set; } = string.Empty;
}

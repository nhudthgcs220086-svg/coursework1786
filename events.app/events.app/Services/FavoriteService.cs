using System.Text.Json;

namespace events.app.Services;

public sealed class FavoriteService
{
    private const string FavoritesKey = "favorite-event-ids";
    private readonly HashSet<string> favoriteIds = LoadIds();

    public bool IsFavorite(string eventId) => favoriteIds.Contains(eventId);

    public void Toggle(string eventId)
    {
        if (!favoriteIds.Add(eventId))
            favoriteIds.Remove(eventId);

        Preferences.Set(FavoritesKey, JsonSerializer.Serialize(favoriteIds));
    }

    private static HashSet<string> LoadIds()
    {
        var stored = Preferences.Get(FavoritesKey, string.Empty);
        if (string.IsNullOrWhiteSpace(stored))
            return [];

        try
        {
            return JsonSerializer.Deserialize<HashSet<string>>(stored) ?? [];
        }
        catch (JsonException)
        {
            Preferences.Remove(FavoritesKey);
            return [];
        }
    }
}

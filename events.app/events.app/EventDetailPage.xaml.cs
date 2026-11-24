using events.app.Models;
using events.app.Services;
using System.Collections.ObjectModel;

namespace events.app;

public partial class EventDetailPage : ContentPage
{
    private readonly Event eventItem;
    private readonly EventService eventService;
    private readonly ObservableCollection<Session> sessions = [];
    private bool sessionsLoaded;

    public EventDetailPage(Event eventItem, EventService eventService)
    {
        InitializeComponent();
        this.eventItem = eventItem;
        this.eventService = eventService;

        TitleLabel.Text = eventItem.Title;
        DateLocationLabel.Text = eventItem.DateAndLocation;
        DescriptionLabel.Text = string.IsNullOrWhiteSpace(eventItem.Description)
            ? "No description available."
            : eventItem.Description;
        TypeLabel.Text = string.IsNullOrWhiteSpace(eventItem.Type) ? string.Empty : $"Type: {eventItem.Type}";
        OrganiserLabel.Text = string.IsNullOrWhiteSpace(eventItem.Organiser) ? string.Empty : $"Organiser: {eventItem.Organiser}";
        ContactLabel.Text = string.Join(" • ", new[]
        {
            eventItem.ContactEmail,
            eventItem.ContactPhone
        }.Where(value => !string.IsNullOrWhiteSpace(value)));
        SessionsCollection.ItemsSource = sessions;
    }

    protected override async void OnAppearing()
    {
        base.OnAppearing();
        if (!sessionsLoaded)
            await LoadSessionsAsync();
    }

    private async Task LoadSessionsAsync()
    {
        SessionLoadingIndicator.IsVisible = true;
        SessionLoadingIndicator.IsRunning = true;

        try
        {
            var allSessions = await eventService.GetSessionsAsync();
            var matchingSessions = allSessions.Where(session =>
                session.EventId == eventItem.Id ||
                session.EventName.Equals(eventItem.Title, StringComparison.OrdinalIgnoreCase) ||
                session.Name.Equals(eventItem.Title, StringComparison.OrdinalIgnoreCase));

            sessions.Clear();
            foreach (var session in matchingSessions)
                sessions.Add(session);

            SessionStatusLabel.Text = sessions.Count == 0 ? "No sessions available for this event." : string.Empty;
            SessionStatusLabel.IsVisible = sessions.Count == 0;
            sessionsLoaded = true;
        }
        catch (Exception ex)
        {
            SessionStatusLabel.Text = $"Could not load sessions: {ex.Message}";
            SessionStatusLabel.IsVisible = true;
        }
        finally
        {
            SessionLoadingIndicator.IsRunning = false;
            SessionLoadingIndicator.IsVisible = false;
        }
    }

    private async void OnSubmitClicked(object? sender, EventArgs e)
    {
        if (string.IsNullOrWhiteSpace(NameEntry.Text) ||
            string.IsNullOrWhiteSpace(EmailEntry.Text) ||
            string.IsNullOrWhiteSpace(PhoneEntry.Text) ||
            string.IsNullOrWhiteSpace(MessageEditor.Text))
        {
            SubmitStatusLabel.Text = "Please complete all fields.";
            SubmitStatusLabel.TextColor = Colors.DarkRed;
            SubmitStatusLabel.IsVisible = true;
            return;
        }

        try
        {
            SubmitButton.IsEnabled = false;
            await eventService.SubmitContactRequestAsync(new ContactRequest
            {
                EventId = eventItem.Id,
                EventName = eventItem.Title,
                Name = NameEntry.Text.Trim(),
                Email = EmailEntry.Text.Trim(),
                Phone = PhoneEntry.Text.Trim(),
                Message = MessageEditor.Text.Trim(),
                CreatedAtUtc = DateTime.UtcNow.ToString("O")
            });

            SubmitStatusLabel.Text = "Your message was sent successfully.";
            SubmitStatusLabel.TextColor = Colors.DarkGreen;
            SubmitStatusLabel.IsVisible = true;
            NameEntry.Text = string.Empty;
            EmailEntry.Text = string.Empty;
            PhoneEntry.Text = string.Empty;
            MessageEditor.Text = string.Empty;
        }
        catch (Exception ex)
        {
            SubmitStatusLabel.Text = $"Could not send message: {ex.Message}";
            SubmitStatusLabel.TextColor = Colors.DarkRed;
            SubmitStatusLabel.IsVisible = true;
        }
        finally
        {
            SubmitButton.IsEnabled = true;
        }
    }
}

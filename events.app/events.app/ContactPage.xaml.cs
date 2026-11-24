using events.app.Models;
using events.app.Services;

namespace events.app;

public partial class ContactPage : ContentPage
{
    private readonly EventService eventService;

    public ContactPage(EventService eventService)
    {
        InitializeComponent();
        this.eventService = eventService;
    }

    private async void OnSubmitClicked(object? sender, EventArgs e)
    {
        if (string.IsNullOrWhiteSpace(NameEntry.Text) ||
            string.IsNullOrWhiteSpace(EmailEntry.Text) ||
            string.IsNullOrWhiteSpace(PhoneEntry.Text) ||
            string.IsNullOrWhiteSpace(MessageEditor.Text))
        {
            StatusLabel.Text = "Please complete all fields.";
            StatusLabel.TextColor = Colors.DarkRed;
            StatusLabel.IsVisible = true;
            return;
        }

        try
        {
            SubmitButton.IsEnabled = false;
            await eventService.SubmitContactRequestAsync(new ContactRequest
            {
                EventName = "General contact",
                Name = NameEntry.Text.Trim(),
                Email = EmailEntry.Text.Trim(),
                Phone = PhoneEntry.Text.Trim(),
                Message = MessageEditor.Text.Trim(),
                CreatedAtUtc = DateTime.UtcNow.ToString("O")
            });

            StatusLabel.Text = "Your message was sent successfully.";
            StatusLabel.TextColor = Colors.DarkGreen;
            StatusLabel.IsVisible = true;
            NameEntry.Text = string.Empty;
            EmailEntry.Text = string.Empty;
            PhoneEntry.Text = string.Empty;
            MessageEditor.Text = string.Empty;
        }
        catch (Exception ex)
        {
            StatusLabel.Text = $"Could not send message: {ex.Message}";
            StatusLabel.TextColor = Colors.DarkRed;
            StatusLabel.IsVisible = true;
        }
        finally
        {
            SubmitButton.IsEnabled = true;
        }
    }
}

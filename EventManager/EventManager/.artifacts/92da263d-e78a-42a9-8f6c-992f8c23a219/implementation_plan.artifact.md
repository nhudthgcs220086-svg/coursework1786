# Implementation Plan - Event Manager Enhancements (Sections A, B, C, D)

Enhancing the Event Manager app to support advanced event details, duplicate validation, confirmation dialogs, session management, event-session relationships with cascade delete, and advanced search.

## User Review Required

> [!IMPORTANT]
> - **Database Schema Version Upgrade:** Room database version will be incremented from `1` to `2`, adding `SessionEntity` and updating `EventEntity`. Destructive migration or schema migration will be configured.
> - **Event Click Behavior:** Clicking an event in `MainActivity` will now open an **Event Detail Activity** (showing all event details, description, contact info, and list of sessions), with an option to edit the event or manage sessions.

## Proposed Changes

### 1. Data Layer (`com.example.eventmanager.data`)
- **[MODIFY] [EventEntity.java](file:///C:/Users/LENOVO/AndroidStudioProjects/EventManager/EventManager/app/src/main/java/com/example/eventmanager/data/EventEntity.java)**
  - Add fields: `description`, `contactEmail`, `contactPhone`.
- **[NEW] [SessionEntity.java](file:///C:/Users/LENOVO/AndroidStudioProjects/EventManager/EventManager/app/src/main/java/com/example/eventmanager/data/SessionEntity.java)**
  - Entity representing sessions with fields: `id` (PK), `eventId` (FK referencing `events(id)` with CASCADE delete), `name`, `time`, `speaker`, `room`, `type`, `description`.
- **[MODIFY] [EventDao.java](file:///C:/Users/LENOVO/AndroidStudioProjects/EventManager/EventManager/app/src/main/java/com/example/eventmanager/data/EventDao.java)**
  - Add query to check duplicate events by name and date: `SELECT * FROM events WHERE LOWER(name) = LOWER(:name) AND date = :date`.
  - Add advanced search queries (by name/description, date, organiser).
- **[NEW] [SessionDao.java](file:///C:/Users/LENOVO/AndroidStudioProjects/EventManager/EventManager/app/src/main/java/com/example/eventmanager/data/SessionDao.java)**
  - DAO for session CRUD operations and querying sessions by event ID or search query.
- **[MODIFY] [AppDatabase.java](file:///C:/Users/LENOVO/AndroidStudioProjects/EventManager/EventManager/app/src/main/java/com/example/eventmanager/data/AppDatabase.java)**
  - Add `SessionEntity` to database entities, increment version to `2`, add `.fallbackToDestructiveMigration()`.

### 2. UI & Feature A: Enhanced Event Form & Validation
- **[MODIFY] [activity_add_event.xml](file:///C:/Users/LENOVO/AndroidStudioProjects/EventManager/EventManager/app/src/main/res/layout/activity_add_event.xml)**
  - Add input fields for Event Description, Contact Email, and Contact Phone.
- **[MODIFY] [AddEventActivity.java](file:///C:/Users/LENOVO/AndroidStudioProjects/EventManager/EventManager/app/src/main/java/com/example/eventmanager/AddEventActivity.java)**
  - Collect new fields.
  - Implement duplicate check logic (name + date).
  - Implement a Confirmation Dialog (`AlertDialog`) before saving/updating to DB.
- **[MODIFY] [strings.xml](file:///C:/Users/LENOVO/AndroidStudioProjects/EventManager/EventManager/app/src/main/res/values/strings.xml)**
  - Add labels, hints, session types, confirmation dialog strings, and search strings.

### 3. Feature B & C: Session Management & Event-Session Association
- **[NEW] [SessionAdapter.java](file:///C:/Users/LENOVO/AndroidStudioProjects/EventManager/EventManager/app/src/main/java/com/example/eventmanager/adapter/SessionAdapter.java)**
  - RecyclerView adapter for listing sessions.
- **[NEW] [AddSessionActivity.java](file:///C:/Users/LENOVO/AndroidStudioProjects/EventManager/EventManager/app/src/main/java/com/example/eventmanager/AddSessionActivity.java)**
  - Activity / Dialog to add or edit a session (Name, Time, Speaker, Room, Type: Workshop, Presentation, Panel Discussion, Networking, Keynote, Others, Description).
- **[NEW] [EventDetailActivity.java](file:///C:/Users/LENOVO/AndroidStudioProjects/EventManager/EventManager/app/src/main/java/com/example/eventmanager/EventDetailActivity.java)**
  - Display full event details (Name, Date, Location, Organiser, Type, Status, Description, Contact Email, Contact Phone).
  - Display total session count and list of sessions with add/edit/delete capability.
- **[NEW] Layout XML files:**
  - `activity_event_detail.xml`
  - `activity_add_session.xml`
  - `item_session.xml`

### 4. Feature D: Advanced Search System
- **[MODIFY] [MainActivity.java](file:///C:/Users/LENOVO/AndroidStudioProjects/EventManager/EventManager/app/src/main/java/com/example/eventmanager/MainActivity.java)**
  - Add SearchView in Toolbar.
  - Add filter dialog/options for searching by date or organiser.
  - Implement search logic across name, description, date, and organiser.

## Verification Plan

### Automated Tests
- Build project via Gradle (`app:assembleDebug`) to ensure zero compilation or Room annotation processor errors.

### Manual Verification
- Run app on emulator/device.
- Test adding an event with description, email, phone, duplicate checking, and confirmation dialog.
- Test viewing event details, adding/editing/deleting sessions, and verifying cascade delete when deleting an event.
- Test advanced search by name, description, date, and organiser.

# Campus Events workshop

This implements the Campus Events feed and failure exercise from PDF pages 12-13
(the slide footers are numbered 13-14).

## API contract

GET https://6ad70838f71f154ac4879fac.mockapi.io/api/v1/events/event

The response is a JSON array. EventDto maps `name` to `title`, `startingDate` to
`startsAt`, and `address` to nullable `location` using @SerialName. The feed only
needs those fields and `id`; other response fields are ignored. Required fields
must have the expected JSON types. The repository maps a missing/null location
to `TBA`, as in the slides. The time label uses the server value, as in the slides.

## What Refresh and Retry do

1. EventsScreen calls the supplied onRefresh callback.
2. EventsViewModel sets Loading and launches work in viewModelScope.
3. EventsRepository calls EventsRemoteDataSource, which calls CampusApi.
4. Retrofit performs GET `event` relative to the HTTPS base URL ending `/events/`.
5. Kotlin serialization reads EventDto objects; EventsRepository maps CampusEvent.
6. EventsViewModel publishes Content, Empty, or a classified Error.
7. Compose renders that state. Retry runs the same operation after a failure.

The app initially opens Events. Tasks opens the existing local task screen,
including its dark-mode switch. Networking is read-only, with no caching, sync,
authentication, pagination, or background refresh.

## Failure exercise

| Try | Expected result |
| --- | --- |
| Disconnect the device/emulator network and tap Refresh | Connection error and Retry; no crash |
| Temporarily use an unreachable HTTPS host in CampusApiClient.BASE_URL, rebuild and run | Retryable connection error |
| Temporarily use a nonexistent base path on the same host, rebuild and run | HTTP request error and Retry |
| Return `[]` | Explicit empty-content message, with Refresh available |
| Restore the valid endpoint/network, then tap Retry (rebuild first if source changed) | Content returns |
| Return malformed JSON or omit a required field | Safe data-error message and Retry |
| Return HTTP 5xx | Server-error message inviting retry later |

Restore the supplied base URL after the exercise. Do not change or delete shared
MockAPI records to simulate an empty response. The automated tests supply `[]`
locally and simulate failures without modifying the remote service.

## Verification

Run `gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:lintDebug`.

EventsRepositoryTest checks the actual JSON field mapping, extra fields, nullable
locations, an empty response, and required fields. EventsViewModelTest checks
loading/content, repeated refresh, empty content, no connection, timeout, HTTP
404/503, invalid data, cancellation, and recovery after Retry. The coroutine test
dependency is only used by local tests; the app uses the layers from the slides.

Verified on 20 October 2026: the debug build succeeded; all 14 local unit tests
passed; Android lint completed with no errors (dependency-update and manifest
warnings remain). A temporary Pixel 9 emulator confirmed live API content,
a connection error with Retry after disabling its network, content recovery
after reconnecting and tapping Retry, and access to the existing Tasks screen.

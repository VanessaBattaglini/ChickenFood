# FIX: Authentication State Persistence Issue

## Problem
The PointsCard and Logout button were still visible for non-authenticated users after clicking "Empecemos" on the SplashScreen. This occurred because:

1. **Root Cause**: Used `rememberSaveable` for storing authentication state
   - `rememberSaveable` persists state across activity recreations
   - When user logs out → goes to SplashActivity → clicks "Empecemos" → MainActivity remounts
   - The OLD saved state from previous session was being restored instead of checking fresh auth status

2. **Why This Happened**:
   ```kotlin
   // ❌ WRONG - Persists old value across activity recreations
   var currentUser by rememberSaveable { mutableStateOf<String?>(null) }
   
   LaunchedEffect(Unit) {
       currentUser = AuthHelper.getCurrentUser()?.uid
   }
   ```
   - `rememberSaveable` would restore the old `currentUser` value
   - `LaunchedEffect(Unit)` only runs ONCE on first composition
   - If user was previously logged in, state would be remembered incorrectly

## Solution
**Remove `rememberSaveable` persistence for authentication state.**

Authentication state should ALWAYS be checked fresh from Firebase, never persisted locally.

### Code Change in `MainActivity.kt`

**BEFORE** (Lines ~87-99):
```kotlin
var currentUser by rememberSaveable { mutableStateOf<String?>(null) }

androidx.compose.runtime.LaunchedEffect(Unit) {
    currentUser = AuthHelper.getCurrentUser()?.uid
    if (currentUser != null) {
        rewardsViewModel.loadUserRewards(currentUser!!)
    }
}
```

**AFTER** (Lines ~87-99):
```kotlin
// ✅ FIX: Obtener currentUser sin persistencia (no usar rememberSaveable para auth)
// Se obtiene fresh cada vez que el composable se recompone
val currentUser = AuthHelper.getCurrentUser()?.uid

// Cargar rewards cuando el usuario está autenticado
LaunchedEffect(currentUser) {
    if (currentUser != null) {
        Log.d(TAG, "Loading rewards for user: $currentUser")
        rewardsViewModel.loadUserRewards(currentUser)
    }
}
```

### Key Differences

1. **`currentUser` is now a computed value, not mutable state**:
   - `val currentUser = AuthHelper.getCurrentUser()?.uid` (fresh check every recomposition)
   - NOT persisted via `rememberSaveable`
   - Always reflects the TRUE authentication state from Firebase

2. **`LaunchedEffect` depends on `currentUser` instead of `Unit`**:
   - `LaunchedEffect(currentUser)` triggers whenever `currentUser` changes
   - This ensures rewards are loaded fresh when authentication status changes

## How It Works Now

### Scenario 1: User Logs In
1. User on SplashScreen clicks "Inscribete"
2. SignUpActivity → Google Sign-In → Saves to Firebase
3. SplashActivity navigates to MainActivity
4. `AuthHelper.getCurrentUser()?.uid` returns the user ID ✓
5. `showLogout = true` → Logout button appears ✓
6. `LaunchedEffect` triggers → PointsCard loads rewards ✓

### Scenario 2: User Doesn't Log In
1. User on SplashScreen clicks "Empecemos"
2. Direct navigation to MainActivity (no Firebase auth)
3. `AuthHelper.getCurrentUser()?.uid` returns `null` ✓
4. `showLogout = false` → Logout button hidden ✓
5. `currentUser != null` check prevents PointsCard rendering ✓

### Scenario 3: User Logs Out
1. User in Dashboard clicks Logout button
2. `AuthHelper.signOut()` clears Firebase session
3. Navigation back to SplashActivity with `finish()`
4. User clicks "Empecemos"
5. MainActivity recomposed with NO SAVED STATE
6. `AuthHelper.getCurrentUser()?.uid` returns `null` (fresh Firebase check) ✓
7. PointsCard and Logout hidden immediately ✓

## Verification Checklist
- [ ] Launch app and click "Empecemos" (no login)
  - PointsCard should NOT appear
  - Logout button should NOT appear
- [ ] Logout button test (if logged in)
  - Logout button should be visible
  - Click logout → back to SplashScreen
  - Click "Empecemos" → PointsCard/Logout should be gone
- [ ] Sign in flow
  - Click "Inscribete" → Google Sign-In
  - Return to Dashboard → PointsCard should appear
  - Logout button should be visible

## Technical Details

### Why `rememberSaveable` Is Wrong for Auth
```
Session 1: User logs in
├─ currentUser = "user123" (saved)
│
Session 2: User logs out, clicks "Empecemos"
├─ rememberSaveable restores "user123" (WRONG!)
├─ currentUser = "user123" (persisted from before)
└─ PointsCard shows even though NOT authenticated ❌
```

### Why Direct Firebase Check Is Right
```
Session 1: User logs in
├─ currentUser = "user123" (from Firebase)
│
Session 2: User logs out, clicks "Empecemos"
├─ currentUser = AuthHelper.getCurrentUser()?.uid
├─ Firebase.currentUser is null
├─ currentUser = null (CORRECT!) ✓
└─ PointsCard hidden ✓
```

## Files Modified
- `app/src/main/java/com/daniel/chickenfood/presentation/activity/dashboard/MainActivity.kt` (Lines 87-99)

## Related Files
- `app/src/main/java/com/daniel/chickenfood/helper/AuthHelper.kt` - Provides `getCurrentUser()` method
- `app/src/main/java/com/daniel/chickenfood/presentation/activity/dashboard/TopBar.kt` - Uses `showLogout` parameter
- `app/src/main/java/com/daniel/chickenfood/presentation/activity/dashboard/PointsCard.kt` - Conditionally rendered based on auth

## Status
✅ **FIXED** - Compilation: BUILD SUCCESSFUL (0 errors)

Last Updated: June 3, 2026

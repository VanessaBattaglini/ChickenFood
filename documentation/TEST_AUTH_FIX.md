# Testing Guide: Auth State Fix

## Quick Summary
**Fixed**: PointsCard and Logout button now properly disappear for unauthenticated users.

**Root Cause**: `rememberSaveable` was persisting old authentication state across activity recreations.

**Solution**: Check Firebase auth status fresh on every recomposition (don't persist it).

---

## Test Case 1: User with "Empecemos" (No Auth)

### Steps
1. Launch app
2. Click **"Empecemos"** button on SplashScreen
3. Look at Dashboard

### Expected Result ✅
```
Dashboard should show:
- SearchBar (with search field)
- Banner (image carousel)
- Categories (Pollo Asado, Alitas, etc.)

Dashboard should NOT show:
- PointsCard (no points info)
- Logout button (no logout icon in top-right)
```

### What You'll See
- Top-right has: Settings icon + Notifications bell (NO logout icon) ✅
- No points card below SearchBar ✅

---

## Test Case 2: User with "Inscribete" (Authenticated)

### Steps
1. Launch app
2. Click **"Inscribete"** button on SplashScreen
3. Google Sign-In appears
4. Select account
5. Return to Dashboard

### Expected Result ✅
```
Dashboard should show:
- SearchBar
- PointsCard (shows points, level, progress)
- Logout button (in top-right)
- Banner
- Categories
```

### What You'll See
- Top-right has: Settings icon + Notifications bell + **Logout icon** ✅
- PointsCard visible below SearchBar showing:
  - Points balance (e.g., "1250 puntos disponibles")
  - User level (Regular, Bronce, Plata, etc.)
  - Progress bar to next level
  - Emoji badge

---

## Test Case 3: Logout Flow (Most Critical)

### Steps
1. Authenticate (click "Inscribete" → Google Sign-In)
2. Verify PointsCard and Logout button are visible
3. **Click Logout button** (top-right, after notifications bell)
4. Return to SplashScreen
5. Click **"Empecemos"** again
6. Go to Dashboard

### Expected Result ✅
```
After logout:
- Returned to SplashScreen ✓

After clicking "Empecemos":
- PointsCard hidden ✓
- Logout button hidden ✓
- Only SearchBar, Banner, Categories visible ✓
```

### Why This Tests the Fix
This is the critical scenario that was broken before:
- **Before Fix**: After logout + "Empecemos", PointsCard/Logout were still visible (wrong!)
- **After Fix**: Auth state is checked fresh from Firebase, so they're hidden (correct!)

---

## Test Case 4: Quick Auth Toggle

### Steps
1. Launch app → "Empecemos"
2. Verify NO PointsCard/Logout ✓
3. Force-stop app or navigate back to Splash
4. Click "Inscribete" → Authenticate
5. Return to Dashboard

### Expected Result ✅
```
PointsCard and Logout should NOW be visible ✓
```

---

## What Changed in Code

### File: `MainActivity.kt` (Lines 87-99)

**BEFORE** ❌
```kotlin
// State was saved and restored
var currentUser by rememberSaveable { mutableStateOf<String?>(null) }

// Only checked once at mount
LaunchedEffect(Unit) {
    currentUser = AuthHelper.getCurrentUser()?.uid
}
```

**AFTER** ✅
```kotlin
// Fresh check every recomposition (no persistence)
val currentUser = AuthHelper.getCurrentUser()?.uid

// Triggers whenever currentUser changes
LaunchedEffect(currentUser) {
    if (currentUser != null) {
        rewardsViewModel.loadUserRewards(currentUser)
    }
}
```

### Why This Works

| Scenario | Before | After |
|----------|--------|-------|
| No Auth + Dashboard | Depends on `rememberSaveable` | Firebase says null ✓ |
| Auth + Dashboard | Depends on `rememberSaveable` | Firebase says uid ✓ |
| Logout + "Empecemos" | Still has old saved state ❌ | Firebase says null ✓ |
| Logout + "Inscribete" | Still has old saved state ❌ | Firebase says uid ✓ |

---

## Verification Checklist

After testing, verify:

- [ ] Test Case 1: "Empecemos" → No PointsCard/Logout
- [ ] Test Case 2: "Inscribete" → PointsCard + Logout visible
- [ ] Test Case 3: Logout flow works correctly
- [ ] Test Case 4: Can toggle between auth states

---

## Debugging Tips

If something doesn't work as expected:

### Check Firebase Auth Status
```
In Android Studio Logcat, search for:
"AuthHelper" or "MainActivity"

Look for log messages like:
D/AuthHelper: isUserLoggedIn: true
D/AuthHelper: isUserLoggedIn: false
```

### Check PointsCard Visibility
```
In MainActivity.kt around line 130:
if (currentUser != null) {
    item { PointsCard(...) }
}

This should match whether user is authenticated
```

### Check Logout Button Visibility
```
In MainActivity.kt around line 118:
TopBar(
    showLogout = currentUser != null,
    ...
)

This should match whether user is authenticated
```

---

## Common Issues & Solutions

### Issue: Still Showing PointsCard When Logged Out
**Solution**: 
- Force-stop app completely
- Clear app cache: Settings → Apps → ChickenFood → Storage → Clear Cache
- Relaunch app

### Issue: Logout Button Not Disappearing
**Solution**:
- Verify `AuthHelper.signOut()` is being called
- Check Logcat for logout confirmation
- Verify Firebase Console shows user signed out

### Issue: PointsCard Not Loading After Login
**Solution**:
- User rewards might not exist in Firebase yet
- Check if `rewardsViewModel.loadUserRewards()` is being called
- Verify user has completed at least one order (to generate rewards)

---

## Related Documentation

- 📄 `FIX_AUTH_STATE_PERSISTENCE.md` - Technical details of the fix
- 📄 `UI_CONDICIONADA_AUTENTICACION.md` - Auth-based UI conditioning
- 📄 `02_AUTENTICACION.md` - Authentication architecture
- 📄 `05_SISTEMA_PUNTOS.md` - Points system details

---

## Status
✅ **BUILD SUCCESSFUL** (0 errors)  
✅ **READY FOR TESTING**

Last Updated: June 3, 2026

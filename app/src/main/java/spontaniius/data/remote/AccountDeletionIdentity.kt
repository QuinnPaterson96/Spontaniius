package spontaniius.data.remote

/** In-memory request tag; never log or persist the signed token. */
class AccountDeletionIdentity(val uid: String, val signedToken: String)

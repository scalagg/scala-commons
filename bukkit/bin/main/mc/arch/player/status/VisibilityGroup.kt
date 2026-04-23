package mc.arch.player.status

/**
 * Represents different visibility groups for player visibility management.
 * Used to categorize players for showing/hiding based on settings.
 *
 * @author Limo
 * @date 2/5/2026
 */
enum class VisibilityGroup(val displayName: String)
{
    ALL("All Players"),
    FRIENDS("Friends Only"),
    PARTY("Party Only"),
    NONE("No Players"),
    STAFF("Staff Only"),
    VIP("VIP Only"),
    ADMIN("Admin Only"),
    OPERATOR("Operator Only");
    
    fun includes(other: VisibilityGroup): Boolean
    {
        return when (this)
        {
            ALL -> true
            FRIENDS -> other == FRIENDS || other == PARTY
            PARTY -> other == PARTY
            STAFF -> other == STAFF || other == ALL
            VIP -> other == VIP || other == ALL
            ADMIN -> other == ADMIN || other == STAFF || other == ALL
            OPERATOR -> other == OPERATOR || other == ADMIN || other == STAFF || other == ALL
            NONE -> false
        }
    }
    
    companion object
    {
        fun fromString(value: String): VisibilityGroup
        {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: ALL
        }
    }
}

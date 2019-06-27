sealed class Komparison {
  class LessThan(val value: Int) : Komparison()
  class Exactly(val value: Int) : Komparison()
  class GreaterThan(val value: Int) : Komparison()
  class Repeat(val everyX: Int, val forAMaximumOfYTimes: Int = 1) : Komparison()
}

interface Kountable {
  fun getPreferences(): SharedPreferences
  fun deleteAll()
  fun increment(key: String)
  fun decrement(key: String)
  fun exists(key: String): Boolean
  fun matches(key: String, amount: Komparison): Boolean
  fun count(key: String): Int?
  fun debugKeys()
}

class Kount(private val sharedPreferences: SharedPreferences) : Kountable {

  private val prefix: String = "SPK_"

  internal fun String.prefixed(): String {
    return "$prefix$this"
  }

  override fun getPreferences(): SharedPreferences {
    return sharedPreferences
  }

  private fun keys(): Map<String, Any?> {
    return sharedPreferences.all.filter { it.key.startsWith(prefix) }
  }

  override fun debugKeys() {
    keys().forEach {
      Log.i("Kount", "K: ${it.key} | V: ${it.value}")
    }
  }

  override fun deleteAll() {
    keys().forEach {
      sharedPreferences.edit().remove(it.key).apply()
    }
  }

  override fun count(key: String): Int? {
    val counter = sharedPreferences.getInt(key.prefixed(), Int.MIN_VALUE)
    if (counter == Int.MIN_VALUE) {
      return null
    }
    return counter
  }

  override fun increment(key: String) {
    val currentValue = sharedPreferences.getInt(key.prefixed(), 0)
    sharedPreferences.edit().putInt(key.prefixed(), currentValue + 1).apply()
  }

  override fun decrement(key: String) {
    val currentValue = sharedPreferences.getInt(key.prefixed(), 0)
    sharedPreferences.edit().putInt(key.prefixed(), currentValue - 1).apply()
  }

  override fun exists(key: String): Boolean {
    return sharedPreferences.contains(key.prefixed())
  }

  override fun matches(key: String, amount: Komparison): Boolean {
    val value = count(key) ?: 0
    var matches = false

    when (amount) {
      is Komparison.Exactly -> matches = (value == amount.value)
      is Komparison.LessThan -> matches = (value < amount.value)
      is Komparison.GreaterThan -> matches = (value > amount.value)
      is Komparison.Repeat -> {
        if (value <= 0 || amount.forAMaximumOfYTimes <= 0) {
          matches = false
        } else if (value > amount.everyX * amount.forAMaximumOfYTimes) {
          matches = false
        } else if (value % amount.everyX == 0) {
          matches = true
        }
      }
    }

    increment(key)
    return matches
  }
}

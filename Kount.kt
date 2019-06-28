sealed class Komparison {
  class LessThan(val value: Int) : Komparison()
  class Exactly(val value: Int) : Komparison()
  class GreaterThan(val value: Int) : Komparison()
  class Repeat(val everyX: Int, val forAMaximumOfYTimes: Int) : Komparison()
  class DoRepeat(val everyX: Int, val forAMaximumOfYTimes: Int) : Komparison()
}

interface Kountable {
  fun getPreferences(): SharedPreferences
  fun deleteAll()
  fun increment(key: String)
  fun decrement(key: String)
  fun exists(key: String): Boolean
  fun count(key: String): Int?
  fun debugKeys()
  fun matches(key: String, amount: Komparison): Boolean
  fun justMatch(key: String, amount: Komparison): Boolean
  fun keys(): Map<String, Any?>
}

class Kount(private val sharedPreferences: SharedPreferences) : Kountable {

  private val prefix: String = "ZC_"

  private fun String.prefixed(): String {
    return "$prefix$this"
  }

  override fun getPreferences(): SharedPreferences {
    return sharedPreferences
  }

  override fun keys(): Map<String, Any?> {
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

  private fun matches(key: String, amount: Komparison, autoIncrement: Boolean): Boolean {
    val value = count(key) ?: 0

    if (value == 0 && amount is Komparison.DoRepeat) {
      return true
    }

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
      is Komparison.DoRepeat -> {
        if (value > amount.everyX * amount.forAMaximumOfYTimes) {
          matches = false
        } else if (value % amount.everyX == 0) {
          matches = true
        }
      }
    }

    if (autoIncrement) {
      increment(key)
    }
    return matches
  }

  override fun matches(key: String, amount: Komparison): Boolean {
    return matches(key, amount, true)
  }

  override fun justMatch(key: String, amount: Komparison): Boolean {
    return matches(key, amount, false)
  }
}

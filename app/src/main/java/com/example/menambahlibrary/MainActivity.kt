import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_form.*
import android.widget.TextView
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)

        btnSubmit.setOnClickListener {
            val name = etName.text.toString()
            val email = etEmail.text.toString()
            val age = etAge.text.toString().toInt()
            val gender = if (rbMale.isChecked) "Male" else "Female"
            val address = etAddress.text.toString()

            val intent = Intent(this, DisplayDataActivity::class.java)
            intent.putExtra("NAME", name)
            intent.putExtra("EMAIL", email)
            intent.putExtra("AGE", age)
            intent.putExtra("GENDER", gender)
            intent.putExtra("ADDRESS", address)
            startActivity(intent)
        }

        val textViewResult: TextView = findViewById(R.id.text_view_result)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val jsonPlaceholderApi = retrofit.create(JsonPlaceholderApi::class.java)
        val call = jsonPlaceholderApi.getPosts()

        call.enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                if (!response.isSuccessful) {
                    textViewResult.text = "Code: ${response.code()}"
                    return
                }

                val posts = response.body()
                if (posts != null) {
                    for (post in posts) {
                        var content = ""
                        content += "ID: ${post.id}\n"
                        content += "User ID: ${post.userId}\n"
                        content += "Title: ${post.title}\n"
                        content += "Text: ${post.text}\n\n"
                        textViewResult.append(content)
                    }
                }
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                textViewResult.text = t.message
            }
        })
    }
}

interface JsonPlaceholderApi {
    @GET("posts")
    fun getPosts(): Call<List<Post>>
}

data class Post(
    val id: Int,
    @SerializedName("user_id") val userId: Int,
    val title: String,
    @SerializedName("body") val text: String
)
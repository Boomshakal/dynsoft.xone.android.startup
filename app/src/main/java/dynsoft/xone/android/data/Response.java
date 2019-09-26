package dynsoft.xone.android.data;

public class Response {

	 public boolean HasError;

     public String Error;
     
     public String ErrorDetail;

     public String Session;

     public String Data;
     
     public void FromErrorResult(Result result)
     {
         this.HasError = result.HasError;
         this.Error = result.Error;
         this.ErrorDetail = result.ErrorDetails;
     }
}

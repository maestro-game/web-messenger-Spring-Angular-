import {Component} from '@angular/core';
import {NgForm} from '@angular/forms';
import {HttpService} from '../_service/http.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-sign-up',
  templateUrl: './sign-up.component.html',
  styleUrls: ['./sign-up.component.css']
})
export class SignUpComponent {
  error: string;

  constructor(private httpService: HttpService, public router: Router) {
  }

  redirect(): boolean {
    this.router.navigate(['signIn']);
    return false;
  }

  submit(form: NgForm): void {
    this.httpService.sendSignUpForm(form).subscribe(() => {
        this.router.navigate(['signIn'], {queryParams: {info: 'На вашу почту отправлено письмо с кодом подтверждения'}});
      },
      error => {
        this.error = error.error;
      });
  }
}

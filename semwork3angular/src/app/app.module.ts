import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {FormsModule} from '@angular/forms';
import {AppComponent} from './app.component';
import {SignUpComponent} from './sign-up/sign-up.component';
import {HttpClientModule} from '@angular/common/http';
import {RouterModule, Routes} from '@angular/router';
import {MainComponent} from './main/main.component';
import {SignInComponent} from './sign-in/sign-in.component';
import {SourceComponent} from './source/source.component';
import {HttpService} from './_service/http.service';
import {TokenService} from './_service/token.service';

const appRoutes: Routes = [
  // TODO canActivate
  {path: 'im', component: MainComponent, children: [{
      path: ':id', component: SourceComponent
    }]},
  {path: 'signIn', component: SignInComponent},
  {path: 'signUp', component: SignUpComponent}
  // TODO add similar canActivate
  // {path: '**', redirectTo: 'im'},
  // {path: '**', redirectTo: 'signIn'},
];

@NgModule({
  declarations: [
    AppComponent,
    SignUpComponent,
    MainComponent,
    SignInComponent,
    SourceComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpClientModule,
    RouterModule.forRoot(appRoutes)
  ],
  providers: [HttpService, TokenService],
  bootstrap: [AppComponent]
})
export class AppModule {
}
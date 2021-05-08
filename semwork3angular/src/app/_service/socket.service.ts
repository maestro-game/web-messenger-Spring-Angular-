import {Injectable, OnDestroy} from '@angular/core';
import {TokenService} from './token.service';
import {BehaviorSubject} from 'rxjs';

declare var SockJS;
declare var Stomp;

@Injectable()
export class SocketService implements OnDestroy {
  private stompClient;
  public isConnected = new BehaviorSubject<boolean>(false);

  constructor() {
    const ws = new SockJS('http://localhost:8080/ws');
    this.stompClient = Stomp.over(ws);
    this.stompClient.connect({user: 'user'}, () => {
      this.isConnected.next(true);
    }, () => {
      this.isConnected.next(false);
    });
  }

  subscribe(dest: string, callback: (data) => any): void {
    this.stompClient.subscribe(dest, callback);
  }

  send(dest: string, body: any): void {
    this.stompClient.send(dest, {}, body);
  }

  unsubscribe(dest: string): void {
    this.stompClient.unsubscribe(dest);
  }

  ngOnDestroy(): void {
    this.stompClient.disconnect();
  }
}

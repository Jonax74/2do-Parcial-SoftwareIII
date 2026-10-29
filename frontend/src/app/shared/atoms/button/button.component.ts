import { Component, Input } from '@angular/core';

@Component({
  selector: 'pa-button',
  standalone: true,
  template: `
    <button [type]="type" [disabled]="disabled" [class.secondary]="variant === 'secondary'">
      <ng-content />
    </button>
  `,
  styleUrl: './button.component.scss',
})
export class ButtonComponent {
  @Input() type: 'button' | 'submit' = 'button';
  @Input() disabled = false;
  @Input() variant: 'primary' | 'secondary' = 'primary';
}

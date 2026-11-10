import { Component, Input } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { DigitsOnlyDirective } from '../../directives/digits-only.directive';

@Component({
  selector: 'pa-input-text',
  standalone: true,
  imports: [DigitsOnlyDirective, ReactiveFormsModule],
  template: `
    <input
      [id]="inputId"
      [type]="type"
      [placeholder]="placeholder"
      [autocomplete]="autocomplete"
      [attr.maxlength]="maxLength ?? null"
      [attr.inputmode]="inputMode ?? null"
      [paDigitsOnly]="digitsOnly"
      [formControl]="control"
    />
  `,
  styleUrl: './input-text.component.scss',
})
export class InputTextComponent {
  @Input({ required: true }) inputId = '';
  @Input() type: 'text' | 'email' | 'password' = 'text';
  @Input() placeholder = '';
  @Input() autocomplete = 'off';
  @Input() maxLength?: number;
  @Input() inputMode?: 'numeric' | 'text';
  @Input() digitsOnly = false;
  @Input({ required: true }) control!: FormControl<string>;
}

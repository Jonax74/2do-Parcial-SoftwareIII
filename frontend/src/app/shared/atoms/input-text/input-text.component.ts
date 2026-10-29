import { Component, Input } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'pa-input-text',
  standalone: true,
  imports: [ReactiveFormsModule],
  template: `
    <input
      [id]="inputId"
      [type]="type"
      [placeholder]="placeholder"
      [autocomplete]="autocomplete"
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
  @Input({ required: true }) control!: FormControl<string>;
}

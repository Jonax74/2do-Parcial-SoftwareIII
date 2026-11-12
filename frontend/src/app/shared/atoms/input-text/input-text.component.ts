import { Component, Input } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { DigitsOnlyDirective } from '../../directives/digits-only.directive';

@Component({
  selector: 'pa-input-text',
  standalone: true,
  imports: [DigitsOnlyDirective, ReactiveFormsModule],
  templateUrl: './input-text.component.html',
  styleUrl: './input-text.component.css',
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

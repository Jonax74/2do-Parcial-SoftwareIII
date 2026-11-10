import { Directive, HostListener, Input } from '@angular/core';

@Directive({
  selector: '[paDigitsOnly]',
  standalone: true,
})
export class DigitsOnlyDirective {
  @Input() paDigitsOnly = true;

  private updatingInput = false;

  @HostListener('beforeinput', ['$event'])
  protectInput(event: InputEvent): void {
    if (this.paDigitsOnly && event.data && /\D/.test(event.data)) {
      event.preventDefault();
    }
  }

  @HostListener('input', ['$event'])
  sanitizeInput(event: Event): void {
    if (!this.paDigitsOnly || this.updatingInput) {
      return;
    }

    const input = event.target;
    if (!(input instanceof HTMLInputElement)) {
      return;
    }

    const digits = input.value.replace(/\D/g, '');
    if (digits === input.value) {
      return;
    }

    this.updatingInput = true;
    input.value = digits;
    input.dispatchEvent(new Event('input', { bubbles: true }));
    this.updatingInput = false;
  }
}
